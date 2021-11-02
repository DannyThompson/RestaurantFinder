package com.dthompson.api

import com.google.gson.annotations.SerializedName

class DetailsResultResponseBody {
    @SerializedName("result")
    var details: RestaurantPhoneAndPhotosResponseBody = RestaurantPhoneAndPhotosResponseBody()
}