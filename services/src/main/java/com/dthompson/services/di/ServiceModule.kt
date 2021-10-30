package com.dthompson.services.di

import com.dthompson.api.RestaurantApi
import com.dthompson.services.DefaultRestaurantRepo
import com.dthompson.services.RestaurantRepo
import dagger.Module
import dagger.Provides

@Module
class ServiceModule {
    @Provides
    fun provideRestaurantRepo(restaurantApi: RestaurantApi): RestaurantRepo {
        return DefaultRestaurantRepo(restaurantApi)
    }
}