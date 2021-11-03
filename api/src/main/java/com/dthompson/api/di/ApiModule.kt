package com.dthompson.api.di

import com.dthompson.api.ColorfulHttpLoggingInterceptor
import com.dthompson.api.DefaultRestaurantApi
import com.dthompson.api.RestaurantApi
import com.dthompson.api.RestaurantApiService
import com.dthompson.core.BASE_URL
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ApiModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        val loggingInterceptor = ColorfulHttpLoggingInterceptor()
        loggingInterceptor.setLevel(ColorfulHttpLoggingInterceptor.Level.BASIC)
        builder.addInterceptor(loggingInterceptor)
        return builder.build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .build()
    }

    @Provides
    fun provideRestaurantApiService(retrofit: Retrofit): RestaurantApiService {
        return retrofit.create(RestaurantApiService::class.java)
    }

    @Provides
    fun provideRestaurantApi(restaurantApiService: RestaurantApiService): RestaurantApi {
        return DefaultRestaurantApi(restaurantApiService)
    }
}