package com.dthompson.api

import com.dthompson.core.DETAILS_PHONE_AND_PHOTOS
import com.dthompson.core.PhoneAndPhotosDto
import com.dthompson.core.REQUESTED_TYPE
import com.dthompson.core.Restaurant
import io.reactivex.Single
import java.lang.RuntimeException

/**
 * Implementation of Restaurant API that uses the API service to communicate with the server.
 */
class DefaultRestaurantApi(private val apiService: RestaurantApiService): RestaurantApi {
    override fun getRestaurantsForQuery(queryParam: String?, locationString: String): Single<List<Restaurant>> {
        return apiService.getRestaurantDetails(
                queryParam,
                locationString,
                REQUESTED_TYPE,
                BuildConfig.GOOGLE_PLACES_API_KEY
        )
            .map {
                if (it.isSuccessful) {
                    val list = mutableListOf<Restaurant>()
                    for (restaurantResponse in it.body()!!.restaurantResultResponseBody) {
                        list.add(restaurantResponse.toRestaurant())
                    }

                    list
                } else {
                    throw RuntimeException("Error getting restaurant list")
                }
            }
    }

    override fun getPhoneAndPhotos(placeId: String): Single<PhoneAndPhotosDto> {
        return apiService.getRestaurantSpecialDetails(placeId, DETAILS_PHONE_AND_PHOTOS, BuildConfig.GOOGLE_PLACES_API_KEY)
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