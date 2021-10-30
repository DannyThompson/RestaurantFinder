package com.dthompson.api.di

import com.dthompson.core.Restaurant
import com.google.gson.annotations.SerializedName

class RestaurantResultResponseBody {

    @SerializedName("business_status")
    var status: String = ""

    @SerializedName("place_id")
    var id: String = ""

    @SerializedName("formatted_address")
    var address: String = ""

    // todo location
    @SerializedName("icon")
    var iconUrl: String = ""

    @SerializedName("price_level")
    var priceLevel: Int = 0

    @SerializedName("rating")
    var rating: Float = 0f

    @SerializedName("user_ratings_total")
    var ratingCount: Int = 0

    fun toRestaurant(): Restaurant {
        return Restaurant(status, id, address, iconUrl, priceLevel, rating, ratingCount)
    }
}