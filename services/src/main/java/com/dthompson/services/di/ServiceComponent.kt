package com.dthompson.services.di

import com.dthompson.api.di.ApiModule
import com.dthompson.services.RestaurantRepo
import dagger.Component

@Component(
    modules = [
        ApiModule::class,
        ServiceModule::class
    ]
)

interface ServiceComponent {

    fun exposeRestaurantRepo(): RestaurantRepo
}