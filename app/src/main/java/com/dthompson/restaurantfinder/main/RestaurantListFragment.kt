package com.dthompson.restaurantfinder.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dthompson.core.QUERY_TYPE_ALL
import com.dthompson.core.Restaurant
import com.dthompson.restaurantfinder.R
import com.dthompson.restaurantfinder.main.details.RestaurantDetailDialogFragment

const val TAG_DETAIL_DIALOG_FRAGMENT = "TAG_DETAIL_DIALOG_FRAGMENT"
class RestaurantListFragment: Fragment(), RestaurantListAdapter.OnItemClickListener {

    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantListAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_restaurant_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = RestaurantListAdapter()
        progressBar = view.findViewById(R.id.progress_bar)
        recyclerView = view.findViewById(R.id.recycler_view_restaurants_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        adapter.setClickListener(this)
        observeRestaurants()
        observeLoadingState()
    }

    override fun onItemClicked(restaurant: Restaurant) {
        var detailFragment = requireActivity().supportFragmentManager.findFragmentByTag(
            TAG_DETAIL_DIALOG_FRAGMENT) as RestaurantDetailDialogFragment?

        if (detailFragment == null) {
            detailFragment = RestaurantDetailDialogFragment(restaurant)
        }

        if (!detailFragment.isAdded) {
            detailFragment.show(requireActivity().supportFragmentManager, TAG_DETAIL_DIALOG_FRAGMENT)
        }
    }

    private fun observeRestaurants() {
        viewModel.restaurants.observe(viewLifecycleOwner, {
            if (it.queryType == QUERY_TYPE_ALL) {
                adapter.setRestaurantList(it.allLocalRestaurants)
            } else {
                adapter.setRestaurantList(it.restaurantsByName)
            }

        })
    }

    private fun observeLoadingState() {
        viewModel.loading.observe(viewLifecycleOwner, {
            if (it) {
                progressBar.visibility = View.VISIBLE
                adapter.setRestaurantList(emptyList())
            } else {
                progressBar.visibility = View.GONE
            }
        })
    }
}