package com.dthompson.restaurantfinder.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dthompson.core.GOOGLE_PLACES_API_KEY
import com.dthompson.core.PHOTOS_URL
import com.dthompson.core.Restaurant
import com.dthompson.core.StringUtils
import com.dthompson.restaurantfinder.R

class RestaurantListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var restaurants: MutableList<Restaurant> = ArrayList()
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClicked(restaurant: Restaurant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_restaurant, parent, false)
        return RestaurantViewHolder(itemView, object : RestaurantViewHolder.OnRowClickListener {
            override fun onRowClicked(position: Int) {
                listener?.onItemClicked(restaurants[position])
            }
        })
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

    fun setClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    private class RestaurantViewHolder(view: View, onRowClickListener: OnRowClickListener) : RecyclerView.ViewHolder(view) {
        private val restaurantName: TextView = view.findViewById(R.id.text_view_name)
        private val restaurantPrice: TextView = view.findViewById(R.id.text_view_price)
        private val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
        private val ratingCount: TextView = view.findViewById(R.id.text_view_ratings)
        private val hours: TextView = view.findViewById(R.id.text_view_hours)
        private val imageViewThumbnail: ImageView = view.findViewById(R.id.image_view_thumbnail)

        init {
            view.setOnClickListener { onRowClickListener.onRowClicked(adapterPosition) }
        }

        interface OnRowClickListener {
            fun onRowClicked(position: Int)
        }

        fun setItem(restaurant: Restaurant) {
            restaurantName.text = restaurant.name
            val price = restaurant.priceLevel
            val pluralsRes = if (price <= 2) R.plurals.price_cheap else R.plurals.price_expensive
            restaurantPrice.text = StringUtils.getPriceString(restaurant.priceLevel, pluralsRes, itemView.context)

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

            val firstPhotoReference: String? = restaurant.photoReferences?.firstOrNull()
            firstPhotoReference ?: return
            val photoUrl = PHOTOS_URL.replace("{reference}", firstPhotoReference)
                .replace("{key}", GOOGLE_PLACES_API_KEY)
            imageViewThumbnail.load(photoUrl) {
                error(R.drawable.thumbnail_placeholder)
                placeholder(R.drawable.thumbnail_placeholder)
            }
        }
    }
}