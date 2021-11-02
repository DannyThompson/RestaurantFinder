package com.dthompson.api

import com.dthompson.core.*
import io.reactivex.Single
import java.lang.RuntimeException

class DefaultRestaurantApi(private val apiService: RestaurantApiService): RestaurantApi {
    override fun getRestaurantsForQuery(queryParam: String?, locationString: String): Single<List<Restaurant>> {
        return apiService.getRestaurantDetails(
                queryParam,
                locationString,
                REQUESTED_TYPE,
                GOOGLE_PLACES_API_KEY
        )
            .map {
                if (it.isSuccessful) {
                    val list = mutableListOf<Restaurant>()
                    for (restaurantResponse in it.body()!!.restaurantResultResponseBody) {
                        list.add(restaurantResponse.toRestaurant())
                    }

                    list
                } else {
                    throw RuntimeException("Error 2")
                }
            }
    }

    override fun getPhoneAndPhotos(placeId: String): Single<PhoneAndPhotosDto> {
        return apiService.getRestaurantSpecialDetails(placeId, DETAILS_PHONE_AND_PHOTOS, GOOGLE_PLACES_API_KEY)
            .map {
                if (it.isSuccessful) {
                    val details = it.body()!!.details
                    val photoList = ArrayList<String>()
                    for (photo in details.photos) {
                        photoList.add(photo.photoReference)
                    }

                    PhoneAndPhotosDto(photoList, details.phone)
                } else {
                    throw RuntimeException("Error getting phone and photos")
                }
            }
    }
}