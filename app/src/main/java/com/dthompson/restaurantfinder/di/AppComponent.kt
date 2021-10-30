package com.dthompson.restaurantfinder.di

import com.dthompson.restaurantfinder.main.MainActivityViewModel
import com.dthompson.services.di.ServiceComponent
import dagger.Component

@Component(
    modules = [
        AppModule::class
    ],
    dependencies = [
        ServiceComponent::class
    ]
)

interface AppComponent {
    fun inject(mainActivityViewModel: MainActivityViewModel)
}