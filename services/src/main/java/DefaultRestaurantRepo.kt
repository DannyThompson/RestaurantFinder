package com.dthompson.services

import com.dthompson.api.RestaurantApi
import com.dthompson.core.Restaurant
import com.dthompson.services.storage.LocalStorage
import io.reactivex.Observable
import io.reactivex.Single

class DefaultRestaurantRepo(private val restaurantApi: RestaurantApi, private val localStorage: LocalStorage): RestaurantRepo {
    var restaurantMap: MutableMap<String, Restaurant> = HashMap()

    override fun getRestaurants(query: String?, locationString: String): Single<List<Restaurant>> {
        return restaurantApi.getRestaurantsForQuery(query, locationString)
            .flatMapObservable { source: List<Restaurant?>? -> Observable.fromIterable(source)}
            .flatMapSingle {
                restaurantMap[it.id] = it
                getPhoneAndPhotos(it.id)
            }
            .toList()
            .map { list ->
                for (restaurant in list) {
                    restaurantMap[restaurant.id] = restaurant
                }
                list
            }
    }

    override fun addFavorite(placeId: String) {
        localStorage.addFavoriteId(placeId)
    }

    override fun removeFavorite(placeId: String) {
        localStorage.removeFavoriteId(placeId)
    }

    override fun getFavorites(): Set<String> {
        return localStorage.getFavorites()
    }

    private fun getPhoneAndPhotos(placeId: String): Single<Restaurant> {
            return restaurantApi.getPhoneAndPhotos(placeId)
                .map { phoneAndPhotos ->
                    restaurantMap[placeId]?.phone = phoneAndPhotos.phone
                    restaurantMap[placeId]?.photoReferences = phoneAndPhotos.photos
                    restaurantMap[placeId]
                }
        }
}