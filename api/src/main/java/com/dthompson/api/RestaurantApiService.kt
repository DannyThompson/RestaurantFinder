package com.dthompson.api

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantApiService {
    @GET("api/place/textsearch/json")
    fun getRestaurantDetails(@Query("query") queryParam: String,
                             @Query("type") type: String,
                             @Query("key") key: String): Single<Response<RestaurantListResponseBody>>
}