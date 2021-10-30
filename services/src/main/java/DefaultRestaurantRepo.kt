package com.dthompson.services

import com.dthompson.api.RestaurantApi
import com.dthompson.core.Restaurant
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DefaultRestaurantRepo(val restaurantApi: RestaurantApi): RestaurantRepo {

    override fun getRestaurants(query: String, locationString: String): Single<List<Restaurant>> {
        return restaurantApi.getRestaurantsForQuery(query, locationString)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
        // todo: add onErrorResumeNext?
    }
}