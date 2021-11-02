package com.dthompson.services

import com.dthompson.core.Restaurant
import io.reactivex.Single

interface RestaurantRepo {
    fun getRestaurants(query: String?, locationString: String, apiKey: String): Single<List<Restaurant>>
}