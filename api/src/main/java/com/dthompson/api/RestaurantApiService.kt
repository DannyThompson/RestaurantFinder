package com.dthompson.api

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantApiService {
    @GET("api/place/textsearch/json")
    fun getRestaurantDetails(@Query("query") queryParam: String?,
                             @Query("location") location: String,
                             @Query("type") type: String,
                             @Query("key") key: String): Single<Response<RestaurantListResponseBody>>

    @GET("api/place/details/json")
    fun getRestaurantSpecialDetails(@Query("place_id") placeId: String,
                                    @Query("fields") fields: String,
                                    @Query("key") key: String): Single<Response<DetailsResultResponseBody>>

}