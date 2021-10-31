package com.dthompson.restaurantfinder.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dthompson.core.QUERY_TYPE_ALL
import com.dthompson.restaurantfinder.R
import com.dthompson.restaurantfinder.RestaurantFinderApplication
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposables
import io.reactivex.exceptions.Exceptions

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private val viewModel: MainActivityViewModel by viewModels()
    private var permissionDisposable = Disposables.empty()
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantListAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as RestaurantFinderApplication).appComponent!!.inject(viewModel)
        setContentView(R.layout.activity_main)

        adapter = RestaurantListAdapter()
        recyclerView = findViewById(R.id.recycler_view_restaurants_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        toolbar = findViewById(R.id.toolbar)
        toolbar.inflateMenu(R.menu.menu)
        toolbar.setOnMenuItemClickListener(this)
        progressBar = findViewById(R.id.progress_bar)
        searchView = findViewById(R.id.search_view)
        searchView.isSubmitButtonEnabled = true
        // Set mag icon color.
        val icon: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon)
        icon.setColorFilter(resources.getColor(R.color.dark_green, null))

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
        observeRestaurants()
        observeLoadingState()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_refresh) {
            adapter.setRestaurantList(emptyList())
            viewModel.getLocation()
            return true
        }
        return false
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
                                        .setPositiveButton(R.string.allow) { _, _ -> checkLocationPermission() }
                                        .setNegativeButton(R.string.cancel, null)
                                        .show()
                            }

                            else -> {
                                // User has requested to not be asked again. We must have them allow location via settings instead.
                                AlertDialog.Builder(this)
                                        .setTitle(R.string.location_permission_title)
                                        .setMessage(R.string.location_permission_message)
                                        .setCancelable(false)
                                        .setPositiveButton(R.string.settings) { _, _ ->
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

    private fun observeRestaurants() {
        viewModel.restaurants.observe(this, {
            if (it.queryType == QUERY_TYPE_ALL) {
                adapter.setRestaurantList(it.allLocalRestaurants)
            } else {
                adapter.setRestaurantList(it.restaurantsByName)
            }

        })
    }

    private fun observeLoadingState() {
        viewModel.loading.observe(this, {
            if (it) progressBar.visibility = View.VISIBLE else progressBar.visibility = View.GONE
        })
    }
}