package com.dthompson.api

import com.dthompson.core.Restaurant
import io.reactivex.Single

interface RestaurantApi {
    fun getRestaurantsForQuery(queryParam: String?, locationString: String, apiKey: String): Single<List<Restaurant>>
}