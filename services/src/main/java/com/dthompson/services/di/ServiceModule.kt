package com.dthompson.services.di

import android.content.Context
import com.dthompson.api.RestaurantApi
import com.dthompson.services.DefaultRestaurantRepo
import com.dthompson.services.RestaurantRepo
import com.dthompson.services.storage.DefaultLocalStorage
import com.dthompson.services.storage.LocalStorage
import dagger.Module
import dagger.Provides

@Module
class ServiceModule {
    @Provides
    fun provideLocalStorage(context: Context): LocalStorage {
        return DefaultLocalStorage(context)
    }

    @Provides
    fun provideRestaurantRepo(restaurantApi: RestaurantApi, localStorage: LocalStorage): RestaurantRepo {
        return DefaultRestaurantRepo(restaurantApi, localStorage)
    }
}