package com.dthompson.api

import okhttp3.*
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * An OkHttp interceptor which logs request and response information. Can be applied as an
 * [application interceptor][OkHttpClient.interceptors] or as a [ ][OkHttpClient.networkInterceptors].
 *
 * The format of the logs created by
 * this class should not be considered stable and may change slightly between releases. If you need
 * a stable logging format, use your own interceptor.
 *
 *
 * * This is a direct copy/paste/convert to kotlin of [okhttp3.logging.HttpLoggingInterceptor] from 3.14.2
 * * https://github.com/square/okhttp/blob/parent-3.14.2/okhttp-logging-interceptor/src/main/java/okhttp3/logging/HttpLoggingInterceptor.java
 */
class ColorfulHttpLoggingInterceptor @JvmOverloads constructor(private val logger: Logger = Logger.DEFAULT) :
        Interceptor {
    enum class Level {
        /**
         * No logs.
         */
        NONE,

        /**
         * Logs request and response lines.
         *
         *
         * Example:
         * <pre>`--> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
        `</pre> *
         */
        BASIC,

        /**
         * Logs request and response lines and their respective headers.
         *
         *
         * Example:
         * <pre>`--> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
        `</pre> *
         */
        HEADERS,

        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         *
         *
         * Example:
         * <pre>`--> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
        `</pre> *
         */
        BODY
    }

    interface Logger {
        fun log(message: String?)
        fun log(message: String?, isWarning: Boolean)

        companion object {
            /**
             * A [Logger] defaults output appropriate for the current platform.
             */
            val DEFAULT: Logger = object : Logger {
                override fun log(message: String?) {
                    val censoredLogs = censorPassword(message!!)
                    Platform.get().log(censoredLogs, Platform.INFO,null)
                }

                override fun log(message: String?, isWarning: Boolean) {
                    if (isWarning) {
                        logWarning(message!!)
                    } else {
                        log(message)
                    }
                }

                private fun logWarning(message: String) {
                    val censoredLogs = censorPassword(message)
                    Platform.get().log(censoredLogs, Platform.WARN, null)
                }

                private fun censorPassword(input: String): String {
                    var input1 = input
                    if (input1.toLowerCase(Locale.US).contains("password")) {
                        input1 = "LOG CONTAINS PASSWORD; CENSORED"
                    }
                    return input1
                }
            }
        }
    }

    @Volatile
    private var headersToRedact = emptySet<String>()
    fun redactHeader(name: String) {
        val newHeadersToRedact: MutableSet<String> =
                TreeSet(java.lang.String.CASE_INSENSITIVE_ORDER)
        newHeadersToRedact.addAll(headersToRedact)
        newHeadersToRedact.add(name)
        headersToRedact = newHeadersToRedact
    }

    @Volatile
    var level = Level.NONE
        private set

    /**
     * Change the level at which this interceptor logs.
     */
    fun setLevel(level: Level?): ColorfulHttpLoggingInterceptor {
        if (level == null) throw NullPointerException("level == null. Use Level.NONE instead.")
        this.level = level
        return this
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = level
        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }
        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS
        val requestBody = request.body
        val hasRequestBody = requestBody != null
        val connection = chain.connection()
        var requestStartMessage: String = ("--> "
                + request.method
                + ' ' + request.url
                + if (connection != null) " " + connection.protocol() else "")
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody!!.contentLength() + "-byte body)"
        }
        logger.log(requestStartMessage)
        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody!!.contentType() != null) {
                    logger.log("Content-Type: " + requestBody.contentType())
                }
                if (requestBody.contentLength() != -1L) {
                    logger.log("Content-Length: " + requestBody.contentLength())
                }
            }
            val headers = request.headers
            var i = 0
            val count = headers.size
            while (i < count) {
                val name = headers.name(i)
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equals(
                                name,
                                ignoreCase = true
                        ) && !"Content-Length".equals(name, ignoreCase = true)
                ) {
                    logHeader(headers, i, false)
                }
                i++
            }
            if (!logBody || !hasRequestBody) {
                logger.log("--> END " + request.method)
            } else if (bodyHasUnknownEncoding(request.headers)) {
                logger.log("--> END " + request.method + " (encoded body omitted)")
            } else if (requestBody!!.isDuplex()) {
                logger.log("--> END " + request.method + " (duplex request body omitted)")
            } else {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                var charset: Charset? = UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }
                logger.log("")
                if (isPlaintext(buffer)) {
                    logger.log(buffer.readString(charset!!))
                    logger.log(
                            "--> END " + request.method
                                    + " (" + requestBody.contentLength() + "-byte body)"
                    )
                } else {
                    logger.log(
                            ("--> END " + request.method + " (binary "
                                    + requestBody.contentLength() + "-byte body omitted)")
                    )
                }
            }
        }

        // NOTE: only use logWarning after this point since response is below
        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logger.log("<-- HTTP FAILED: $e", true)
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val isWarning = response.code / 100 != 2
        val responseBody = response.body
        val contentLength = responseBody!!.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        logger.log(
                ("<-- "
                        + response.code
                        + (if (response.message
                                .isEmpty()
                ) "" else ' '.toString() + response.message)
                        + ' ' + response.request.url
                        + " (" + tookMs + "ms" + (if (!logHeaders) ", $bodySize body" else "") + ')'),
                isWarning
        )
        if (logHeaders) {
            val headers = response.headers
            var i = 0
            val count = headers.size
            while (i < count) {
                logHeader(headers, i, isWarning)
                i++
            }
            // todo?
            if (!logBody || response.body != null) {
                logger.log("<-- END HTTP", isWarning)
            } else if (bodyHasUnknownEncoding(response.headers)) {
                logger.log("<-- END HTTP (encoded body omitted)", isWarning)
            } else {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                var buffer = source.buffer
                var gzippedLength: Long? = null
                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    gzippedLength = buffer.size
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }
                var charset: Charset? = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }
                if (!isPlaintext(buffer)) {
                    logger.log("", isWarning)
                    logger.log(
                            "<-- END HTTP (binary " + buffer.size + "-byte body omitted)",
                            isWarning
                    )
                    return response
                }
                if (contentLength != 0L) {
                    logger.log("", isWarning)
                    logger.log(buffer.clone().readString(charset!!), isWarning)
                }
                if (gzippedLength != null) {
                    logger.log(
                            ("<-- END HTTP (" + buffer.size + "-byte, "
                                    + gzippedLength + "-gzipped-byte body)"), isWarning
                    )
                } else {
                    logger.log("<-- END HTTP (" + buffer.size + "-byte body)", isWarning)
                }
            }
        }
        return response
    }

    private fun logHeader(headers: Headers, i: Int, isWarning: Boolean) {
        val value = if (headersToRedact.contains(headers.name(i))) "██" else headers.value(i)
        logger.log(headers.name(i) + ": " + value, isWarning)
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        fun isPlaintext(buffer: Buffer): Boolean {
            try {
                val prefix = Buffer()
                val byteCount = if (buffer.size < 64) buffer.size else 64
                buffer.copyTo(prefix, 0, byteCount)
                for (i in 0..15) {
                    if (prefix.exhausted()) {
                        break
                    }
                    val codePoint = prefix.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                        return false
                    }
                }
                return true
            } catch (e: EOFException) {
                return false // Truncated UTF-8 sequence.
            }
        }

        private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
            val contentEncoding = headers["Content-Encoding"]
            return ((contentEncoding != null
                    ) && !contentEncoding.equals("identity", ignoreCase = true)
                    && !contentEncoding.equals("gzip", ignoreCase = true))
        }
    }
}