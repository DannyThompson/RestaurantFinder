package com.dthompson.restaurantfinder.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ViewFlipper
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.dthompson.restaurantfinder.R
import com.dthompson.restaurantfinder.RestaurantFinderApplication
import com.google.android.material.button.MaterialButton
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposables
import io.reactivex.exceptions.Exceptions

private const val FRAGMENT_INDEX_LIST = 0
private const val FRAGMENT_INDEX_MAP = 1
class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private val viewModel: MainActivityViewModel by viewModels()
    private var permissionDisposable = Disposables.empty()
    private lateinit var searchView: SearchView
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var toolbar: Toolbar
    private lateinit var toggleButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as RestaurantFinderApplication).appComponent!!.inject(viewModel)
        setContentView(R.layout.activity_main)

        viewFlipper = findViewById(R.id.view_flipper)
        viewFlipper.displayedChild = FRAGMENT_INDEX_LIST // Always show list first on startup.

        toggleButton = findViewById(R.id.button_list_map_toggle)
        toggleButton.setOnClickListener {
            if (viewFlipper.displayedChild == FRAGMENT_INDEX_LIST) {
                toggleButton.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_location_marker, null)
                toggleButton.text = resources.getText(R.string.map)
                viewFlipper.displayedChild = FRAGMENT_INDEX_MAP
            } else {
                toggleButton.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_list, null)
                toggleButton.text = resources.getText(R.string.list)
                viewFlipper.displayedChild = FRAGMENT_INDEX_LIST
            }
        }

        toolbar = findViewById(R.id.toolbar)
        toolbar.inflateMenu(R.menu.menu)
        toolbar.setOnMenuItemClickListener(this)
        searchView = findViewById(R.id.search_view)
        searchView.isSubmitButtonEnabled = true

        // Set mag icon color.
        val icon: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon)
        icon.setColorFilter(resources.getColor(R.color.dark_green, null))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query ?: return false
                viewModel.getRestaurants(query, viewModel.currentLocation!!.toFormattedString())
                // Hide keyboard if shown.
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                if (inputMethodManager != null && inputMethodManager.isActive) {
                    inputMethodManager.hideSoftInputFromWindow(searchView.windowToken, 0)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        checkLocationPermission()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_refresh) {
            // Clear the current query and re-get location.
            searchView.setQuery("", false)
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
}