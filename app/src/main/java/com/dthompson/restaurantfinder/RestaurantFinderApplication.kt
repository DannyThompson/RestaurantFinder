package com.dthompson.restaurantfinder

import android.app.Application
import com.dthompson.restaurantfinder.di.AppComponent
import com.dthompson.restaurantfinder.di.AppModule
import com.dthompson.restaurantfinder.di.DaggerAppComponent
import com.dthompson.services.di.DaggerServiceComponent
import com.dthompson.services.di.ServiceComponent
import com.dthompson.services.di.ServiceModule

class RestaurantFinderApplication: Application() {
    var appComponent: AppComponent? = null
    override fun onCreate() {
        super.onCreate()
        setupDagger()
    }

    private fun setupDagger() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .serviceComponent(DaggerServiceComponent.builder().build())
            .build()
    }
}