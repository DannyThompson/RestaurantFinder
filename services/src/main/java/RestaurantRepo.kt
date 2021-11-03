package com.dthompson.services

import com.dthompson.core.Restaurant
import io.reactivex.Single

interface RestaurantRepo {
    fun getRestaurants(query: String?, locationString: String): Single<List<Restaurant>>

    fun addFavorite(placeId: String)

    fun removeFavorite(placeId: String)

    fun getFavorites(): Set<String>
}