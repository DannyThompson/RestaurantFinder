package com.dthompson.api

import com.dthompson.api.di.RestaurantResultResponseBody
import com.google.gson.annotations.SerializedName

class RestaurantListResponseBody {
    @SerializedName("result")
    var restaurantResultResponseBody: List<RestaurantResultResponseBody> = ArrayList()
}