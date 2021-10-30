package com.dthompson.core

class Location(val lat: Double, val long: Double) {
    fun toFormattedString(): String {
        return "$lat,$long"
    }
}