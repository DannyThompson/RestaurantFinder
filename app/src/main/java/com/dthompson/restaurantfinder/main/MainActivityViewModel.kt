package com.dthompson.restaurantfinder.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dthompson.core.Location
import com.dthompson.core.QUERY_TYPE_ALL
import com.dthompson.core.QUERY_TYPE_SEARCH
import com.dthompson.core.Restaurant
import com.dthompson.services.RestaurantRepo
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.exceptions.Exceptions
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivityViewModel: ViewModel() {
    @Inject
    lateinit var restaurantRepo: RestaurantRepo
    @Inject
    lateinit var context: Context

    private var disposable = Disposables.empty()

    val restaurants = MutableLiveData<RestaurantsState>()
    val loading = MutableLiveData<Boolean>()
    var restaurantsState = RestaurantsState()
    var currentLocation: Location? = null

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun getRestaurants(query: String?, locationString: String) {
        disposable.dispose()
        disposable = restaurantRepo.getRestaurants(query, locationString)
            .doOnSubscribe { loading.postValue(true) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    restaurantsState = if (query != null) {
                        restaurantsState.copy(restaurantsByName = it, queryType = QUERY_TYPE_SEARCH)
                    } else {
                        restaurantsState.copy(allLocalRestaurants = it, queryType = QUERY_TYPE_ALL)
                    }
                    restaurants.postValue(restaurantsState)
                    loading.postValue(false)
                },
                { Exceptions.propagate(it) }
            )
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        LocationServices.getFusedLocationProviderClient(context)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(result: LocationResult?) {
                    super.onLocationResult(result)
                    LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this)
                    if (result != null && result.locations.size > 0) {
                        val newestLocation = result.locations.last()
                        val lat = newestLocation.latitude
                        val long = newestLocation.longitude
                        currentLocation = Location(lat, long)
                        // Once current location is retrieved, we want to get a list of ALL local restaurants, so pass null as the query param.
                        getRestaurants(null, currentLocation!!.toFormattedString())
                    }
                }
            }, Looper.getMainLooper())
    }
}