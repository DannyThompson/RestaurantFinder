package com.dthompson.core

class Restaurant(
        var name: String,
        var id: String,
        var address: String,
        var priceLevel: Int,
        var rating: Float,
        var ratingCount: Int,
        var isOpen: Boolean,
        var location: Location) {

        var phone: String? = null
        var photoReferences: List<String>? = null
}