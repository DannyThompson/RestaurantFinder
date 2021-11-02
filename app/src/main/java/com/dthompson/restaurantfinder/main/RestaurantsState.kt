package com.dthompson.restaurantfinder.main

import com.dthompson.core.QUERY_TYPE_ALL
import com.dthompson.core.Restaurant

data class RestaurantsState(val restaurantsByName: List<Restaurant> = ArrayList(), val allLocalRestaurants: List<Restaurant> = ArrayList(), val queryType: String = QUERY_TYPE_ALL)