package com.dthompson.api

import android.util.Log
import com.dthompson.core.GOOGLE_PLACES_API_KEY
import com.dthompson.core.REQUESTED_TYPE
import com.dthompson.core.Restaurant
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
                    Log.d("DMT results ", it.body()!!.restaurantResultResponseBody.toString())
                    val list = mutableListOf<Restaurant>()
                    for (restaurantResponse in it.body()!!.restaurantResultResponseBody) {
                        list.add(restaurantResponse.toRestaurant())
                    }
                    list
                } else {
                    throw RuntimeException()
                }
            }
    }
}