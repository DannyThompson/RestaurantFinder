package com.dthompson.restaurantfinder.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dthompson.core.QUERY_TYPE_ALL
import com.dthompson.core.Restaurant
import com.dthompson.restaurantfinder.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class RestaurantMapFragment: Fragment(), OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var mapView: MapView
    private var map: GoogleMap? = null
    private var currentBounds: LatLngBounds? = null
    private var mapLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_restaurant_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = requireView().findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        observeRestaurants()
        observeLoadingState()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.setOnMapLoadedCallback(this)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onMapLoaded() {
        mapLoaded = true
        updateCamera()
    }

    private fun observeRestaurants() {
        viewModel.restaurants.observe(viewLifecycleOwner, {
            if (it.queryType == QUERY_TYPE_ALL) {
                addMarkers(it.allLocalRestaurants)
            } else {
                addMarkers(it.restaurantsByName)
            }

        })
    }

    // Create and add a map marker for each restaurant in the list.
    private fun addMarkers(restaurants: List<Restaurant>) {
        val latLngBoundsBuilder = LatLngBounds.Builder()
        for (restaurant in restaurants) {
            val latLng = LatLng(restaurant.location.lat, restaurant.location.long)
            latLngBoundsBuilder.include(latLng)
            val marker = MarkerOptions()
                    .position(latLng)
                    .title(restaurant.name)

            map!!.addMarker(marker)
        }

        currentBounds = latLngBoundsBuilder.build()

        if (mapLoaded) {
            updateCamera()
        }
    }

    private fun updateCamera() {
        map!!.animateCamera(
                CameraUpdateFactory.newLatLngBounds(currentBounds, 0)
        )
    }

    private fun observeLoadingState() {
        viewModel.loading.observe(this, { isLoading ->
            if (isLoading) map?.clear()
        })
    }
}