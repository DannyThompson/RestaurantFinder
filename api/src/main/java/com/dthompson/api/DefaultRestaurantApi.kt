package com.dthompson.api

import com.dthompson.core.REQUESTED_TYPE
import com.dthompson.core.Restaurant
import io.reactivex.Single
import java.lang.RuntimeException

class DefaultRestaurantApi(private val apiService: RestaurantApiService): RestaurantApi {
    override fun getRestaurantsForQuery(queryParam: String?, locationString: String, apiKey: String): Single<List<Restaurant>> {
        return apiService.getRestaurantDetails(
                queryParam,
                locationString,
                REQUESTED_TYPE,
                apiKey
        )
            .map {
                if (it.isSuccessful) {
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