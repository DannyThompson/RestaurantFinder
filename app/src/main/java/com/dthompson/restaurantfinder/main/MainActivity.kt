package com.dthompson.restaurantfinder.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import com.dthompson.restaurantfinder.R
import com.dthompson.restaurantfinder.RestaurantFinderApplication
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposables
import io.reactivex.exceptions.Exceptions

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private var permissionDisposable = Disposables.empty()
    private lateinit var searchView: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as RestaurantFinderApplication).appComponent!!.inject(viewModel)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.search_view)
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query ?: return false
                viewModel.getRestaurants(query, viewModel.currentLocation!!.toFormattedString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        checkLocationPermission()
        observeResults()
    }

    private fun checkLocationPermission() {
        permissionDisposable.dispose()
        permissionDisposable = RxPermissions(this)
            .requestEach(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe(
                { permission ->
                    when {
                        permission.granted -> {
                            // Called twice for each permission, but we only care if its called once, so just check one of the permissions.
                            if (permission.name == Manifest.permission.ACCESS_COARSE_LOCATION) {
                                viewModel.getLocation()
                                return@subscribe
                            }
                        }

                        permission.shouldShowRequestPermissionRationale -> {
                            AlertDialog.Builder(this)
                                .setTitle(R.string.location_permission_title)
                                .setMessage(R.string.location_permission_message)
                                .setCancelable(false)
                                .setPositiveButton(R.string.allow) {_,_ -> checkLocationPermission() }
                                .setNegativeButton(R.string.cancel, null)
                                .show()
                        }

                        else -> {
                            // User has requested to not be asked again. We must have them allow location via settings instead.
                            AlertDialog.Builder(this)
                                .setTitle(R.string.location_permission_title)
                                .setMessage(R.string.location_permission_message)
                                .setCancelable(false)
                                .setPositiveButton(R.string.settings) {_,_ ->
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    val uri = Uri.fromParts("package", this.packageName, null)
                                    intent.data = uri
                                    startActivity(intent)
                                }
                                .setNegativeButton(R.string.not_now, null)
                                .show()
                        }
                    }
                },
                { Exceptions.propagate(it) }
            )
    }

    private fun observeResults() {
        viewModel.restaurants.observe(this, {
            for (restaurant in it) {
                Log.d("DMT address ", restaurant.address)
            }
        })
    }
}