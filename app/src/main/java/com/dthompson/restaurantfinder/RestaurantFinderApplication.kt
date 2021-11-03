package com.dthompson.restaurantfinder

import android.app.Application
import com.dthompson.restaurantfinder.di.AppComponent
import com.dthompson.restaurantfinder.di.AppModule
import com.dthompson.restaurantfinder.di.DaggerAppComponent
import com.dthompson.services.com.dthompson.services.di.ServiceComponentHolder

class RestaurantFinderApplication: Application() {
    var appComponent: AppComponent? = null
    override fun onCreate() {
        super.onCreate()
        setupDagger()
    }

    private fun setupDagger() {
        ServiceComponentHolder.create(this)
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .serviceComponent(ServiceComponentHolder.getServiceComponent())
            .build()
    }
}