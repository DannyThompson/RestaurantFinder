package com.dthompson.services

import android.util.Log
import com.dthompson.api.RestaurantApi
import com.dthompson.core.Restaurant
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DefaultRestaurantRepo(private val restaurantApi: RestaurantApi): RestaurantRepo {
    var restaurantMap: MutableMap<String, Restaurant> = HashMap()

    override fun getRestaurants(query: String?, locationString: String): Single<List<Restaurant>> {
        return restaurantApi.getRestaurantsForQuery(query, locationString)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .map { list ->
                for (restaurant in list) {
                    restaurantMap[restaurant.id] = restaurant
                }
                list
            }
    }

    override fun getPhoneAndPhotos(placeId: String): Single<Restaurant> {
        val restaurant: Restaurant? = restaurantMap[placeId]
        return if (restaurant?.phoneAndPhotos != null) {
            Log.d("DMT", "Phone and photos already fetched. Using cached Restaurant object.")
            Single.just(restaurant)
        } else {
            Log.d("DMT", "Getting phone and photos from the web.")
            restaurantApi.getPhoneAndPhotos(placeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map { phoneAndPhotos ->
                    restaurantMap[placeId]?.phoneAndPhotos = phoneAndPhotos
                    restaurantMap[placeId]
                }
        }
    }
}