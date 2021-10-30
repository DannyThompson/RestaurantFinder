package com.dthompson.api.di

import com.dthompson.core.Restaurant
import io.reactivex.Single

interface RestaurantApi {
    fun getRestaurantsForQuery(queryParam: String): Single<List<Restaurant>>
}