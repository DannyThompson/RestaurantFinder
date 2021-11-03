package com.dthompson.api

import com.google.gson.annotations.SerializedName

/**
 * Response body that gets the nested results array of all the queried restaurants for a given area.
 */
class RestaurantListResponseBody {
    @SerializedName("results")
    var restaurantResultResponseBody: List<RestaurantResultResponseBody> = ArrayList()
}