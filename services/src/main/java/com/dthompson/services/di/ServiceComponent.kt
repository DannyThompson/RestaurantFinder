package com.dthompson.services.di

import android.content.Context
import com.dthompson.api.di.ApiModule
import com.dthompson.services.RestaurantRepo
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        ApiModule::class,
        ServiceModule::class
    ]
)

interface ServiceComponent {

    fun exposeRestaurantRepo(): RestaurantRepo

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun supplyContext(context: Context): Builder

        fun build(): ServiceComponent
    }
}