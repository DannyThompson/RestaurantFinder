package com.dthompson.restaurantfinder

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RestaurantFinderApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@RestaurantFinderApplication)
//            modules(listOf(appModule, viewModelModule))
        }
    }
}