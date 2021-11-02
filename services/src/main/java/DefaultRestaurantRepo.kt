package com.dthompson.services

import com.dthompson.api.RestaurantApi
import com.dthompson.core.Restaurant
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DefaultRestaurantRepo(private val restaurantApi: RestaurantApi): RestaurantRepo {

    override fun getRestaurants(query: String?, locationString: String, apiKey: String): Single<List<Restaurant>> {
        return restaurantApi.getRestaurantsForQuery(query, locationString, apiKey)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
        // todo: add onErrorResumeNext?
    }
}