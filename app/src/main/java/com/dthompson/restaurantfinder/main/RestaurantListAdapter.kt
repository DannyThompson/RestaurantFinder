package com.dthompson.restaurantfinder.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dthompson.core.Restaurant
import com.dthompson.restaurantfinder.R

class RestaurantListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var restaurants: MutableList<Restaurant> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_restaurant, parent, false)
        return RestaurantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as RestaurantViewHolder
        viewHolder.setItem(restaurants[position])
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    fun setRestaurantList(restaurants: List<Restaurant>) {
        this.restaurants = restaurants.toMutableList()
        notifyDataSetChanged()
    }

    private class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val restaurantName: TextView = view.findViewById(R.id.text_view_name)
        private val restaurantPrice: TextView = view.findViewById(R.id.text_view_price)
        private val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
        private val ratingCount: TextView = view.findViewById(R.id.text_view_ratings)
        private val hours: TextView = view.findViewById(R.id.text_view_hours)

        fun setItem(restaurant: Restaurant) {
            restaurantName.text = restaurant.name
            val price = restaurant.priceLevel
            if (price > 0) restaurantPrice.text = getPriceString(price)
            else restaurantPrice.visibility = View.GONE

            ratingCount.text = String.format(this.itemView.context.getString(R.string.restaurant_total_ratings),
                    restaurant.ratingCount)
            ratingBar.rating = restaurant.rating

            if (restaurant.isOpen) {
                hours.text = this.itemView.context.getString(R.string.open)
                hours.setTextColor(this.itemView.context.getColor(R.color.quantum_googgreen))
            } else {
                hours.text = this.itemView.context.getString(R.string.closed)
                hours.setTextColor(this.itemView.context.getColor(R.color.quantum_googred))
            }
        }

        private fun getPriceString(price: Int): String {
           return if (price <= 2) {
                this.itemView.context.resources.getQuantityString(R.plurals.price_cheap, price)
            } else {
                // Max price indicator is 4, so use that to determine the right plural string to use.
               this.itemView.context.resources.getQuantityString(R.plurals.price_expensive, 4 - price)
            }
        }
    }
}