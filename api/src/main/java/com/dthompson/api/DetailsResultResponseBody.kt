package com.dthompson.api

import com.google.gson.annotations.SerializedName

/**
 * Response body that gets the nested result body of a Restaurants details.
 */
class DetailsResultResponseBody {
    @SerializedName("result")
    var details: RestaurantPhoneAndPhotosResponseBody = RestaurantPhoneAndPhotosResponseBody()
}