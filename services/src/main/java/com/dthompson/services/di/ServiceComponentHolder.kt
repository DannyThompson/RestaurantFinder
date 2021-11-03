package com.dthompson.services.com.dthompson.services.di

import android.content.Context
import com.dthompson.services.di.DaggerServiceComponent
import com.dthompson.services.di.ServiceComponent

object ServiceComponentHolder {
    private var serviceComponent: ServiceComponent? = null

    fun create(context: Context) {
        if (serviceComponent != null) {
            return
        }

        serviceComponent = DaggerServiceComponent.builder()
            .supplyContext(context)
            .build()
    }

    fun getServiceComponent(): ServiceComponent {
        return serviceComponent!!
    }
}