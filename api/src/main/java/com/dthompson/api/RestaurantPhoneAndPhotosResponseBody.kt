package com.dthompson.api

import com.google.gson.annotations.SerializedName

// Some details aren't available on the plain textsearch response, so this response body is to get those extra details.
class RestaurantPhoneAndPhotosResponseBody {
    @SerializedName("formatted_phone_number")
    var phone: String = ""

    @SerializedName("photos")
    var photos: List<PhotosResponseBody> = ArrayList()

    class PhotosResponseBody {
        @SerializedName("photo_reference")
        var photoReference: String = ""
    }
}