package com.dthompson.api

import com.dthompson.core.Location
import com.dthompson.core.Restaurant
import com.google.gson.annotations.SerializedName

class RestaurantResultResponseBody {

    @SerializedName("name")
    var name: String = ""

    @SerializedName("business_status")
    var status: String = ""

    @SerializedName("place_id")
    var id: String = ""

    @SerializedName("formatted_address")
    var address: String = ""

    @SerializedName("icon")
    var iconUrl: String = ""

    @SerializedName("price_level")
    var priceLevel: Int = 0

    @SerializedName("rating")
    var rating: Float = 0f

    @SerializedName("user_ratings_total")
    var ratingCount: Int = 0

    @SerializedName("opening_hours")
    var openHoursResponseBody: OpenHoursResponseBody = OpenHoursResponseBody()

    @SerializedName("geometry")
    var geometryResponseBody: GeometryResponseBody = GeometryResponseBody()

    fun toRestaurant(): Restaurant {
        val lat = geometryResponseBody.locationResponseBody.lat
        val lng = geometryResponseBody.locationResponseBody.lng
        return Restaurant(name, status, id, address, iconUrl, priceLevel, rating, ratingCount, openHoursResponseBody.isOpen, Location(lat, lng))
    }

    class OpenHoursResponseBody {
        @SerializedName("open_now")
        var isOpen: Boolean = false
    }

    class GeometryResponseBody {
        @SerializedName("location")
        var locationResponseBody: LocationResponseBody = LocationResponseBody()
    }

    class LocationResponseBody {
        @SerializedName("lat")
        var lat: Double = 0.0

        @SerializedName("lng")
        var lng: Double = 0.0
    }
}