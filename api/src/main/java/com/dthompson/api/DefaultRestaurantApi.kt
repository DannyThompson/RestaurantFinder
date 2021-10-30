package com.dthompson.api.di

import com.dthompson.api.RestaurantApiService
import com.dthompson.api.com.dthompson.api.RestaurantApi
import com.dthompson.core.Restaurant
import io.reactivex.Single
import java.lang.RuntimeException

private const val GOOGLE_PLACES_API_KEY = "AIzaSyDQSd210wKX_7cz9MELkxhaEOUhFP0AkSk"
private const val REQUESTED_TYPE = "restaurant" // For now, all we care about is restaurants, so just hard code the type.
class DefaultRestaurantApi(private val apiService: RestaurantApiService): RestaurantApi {

    override fun getRestaurantsForQuery(queryParam: String): Single<List<Restaurant>> {
        return apiService.getRestaurantDetails(queryParam, REQUESTED_TYPE, GOOGLE_PLACES_API_KEY)
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