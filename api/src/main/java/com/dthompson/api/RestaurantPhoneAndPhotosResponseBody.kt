package com.dthompson.api

import com.google.gson.annotations.SerializedName

/**
 * Class representing the response body of a restaurants more specific details.
 * Some details aren't available on the plain textsearch response, so only a few things
 * are cared about here (photos, phone).
 */
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