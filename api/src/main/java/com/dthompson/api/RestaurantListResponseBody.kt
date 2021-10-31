package com.dthompson.api

import com.google.gson.annotations.SerializedName

class RestaurantListResponseBody {
    @SerializedName("results")
    var restaurantResultResponseBody: List<RestaurantResultResponseBody> = ArrayList()
}