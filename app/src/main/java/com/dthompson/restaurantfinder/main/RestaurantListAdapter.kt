package com.dthompson.restaurantfinder.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dthompson.core.PHOTOS_URL
import com.dthompson.core.Restaurant
import com.dthompson.restaurantfinder.BuildConfig
import com.dthompson.restaurantfinder.R
import com.dthompson.restaurantfinder.util.StringUtil

/**
 * Basic RecyclerView adapter that manages the list of restaurants.
 */
class RestaurantListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var restaurants: MutableList<Restaurant> = ArrayList()
    private var favorites = mutableSetOf<String>()
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClicked(restaurant: Restaurant)

        fun onFavoriteClicked(placeId: String, favorited: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_restaurant, parent, false)
        return RestaurantViewHolder(itemView, object : RestaurantViewHolder.OnRowClickListener {
            override fun onRowClicked(position: Int) {
                listener?.onItemClicked(restaurants[position])
            }

            override fun onFavoriteClicked(placeId: String, favorited: Boolean) {

                listener?.onFavoriteClicked(placeId, favorited)
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
        for (restaurant in this.restaurants) {
            if (favorites.contains(restaurant.id)) {
                restaurant.isFavorite = true
            }
        }
        notifyDataSetChanged()
    }

    fun setFavorites(favorites: Set<String>) {
        this.favorites = favorites.toMutableSet()
    }

    fun setClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    private class RestaurantViewHolder(view: View, private val onRowClickListener: OnRowClickListener) : RecyclerView.ViewHolder(view) {
        private val restaurantName: TextView = view.findViewById(R.id.text_view_name)
        private val restaurantPrice: TextView = view.findViewById(R.id.text_view_price)
        private val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
        private val ratingCount: TextView = view.findViewById(R.id.text_view_ratings)
        private val hours: TextView = view.findViewById(R.id.text_view_hours)
        private val imageViewThumbnail: ImageView = view.findViewById(R.id.image_view_thumbnail)
        private val imageViewFavorite: ImageView = view.findViewById(R.id.image_view_favorite)

        init {
            view.setOnClickListener { onRowClickListener.onRowClicked(adapterPosition) }
        }

        interface OnRowClickListener {
            fun onRowClicked(position: Int)

            fun onFavoriteClicked(placeId: String, favorited: Boolean)
        }

        fun setItem(restaurant: Restaurant) {
            restaurantName.text = restaurant.name
            val price = restaurant.priceLevel
            val pluralsRes = if (price <= 2) R.plurals.price_cheap else R.plurals.price_expensive
            restaurantPrice.text = StringUtil.getPriceString(restaurant.priceLevel, pluralsRes, itemView.context)

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
                .replace("{key}", BuildConfig.GOOGLE_PLACES_API_KEY)
            imageViewThumbnail.load(photoUrl) {
                error(R.drawable.thumbnail_placeholder)
                placeholder(R.drawable.thumbnail_placeholder)
            }

            toggleFavoriteIcon(restaurant.isFavorite)

            imageViewFavorite.setOnClickListener {
                val updatedFavVal = !restaurant.isFavorite
                restaurant.isFavorite = updatedFavVal
                toggleFavoriteIcon(updatedFavVal)
                onRowClickListener.onFavoriteClicked(restaurant.id, updatedFavVal)
            }
        }

        private fun toggleFavoriteIcon(isFavorite: Boolean) {
            if (isFavorite) {
                imageViewFavorite.setImageDrawable(ResourcesCompat.getDrawable(itemView.resources, R.drawable.ic_favorited, null))
            } else {
                imageViewFavorite.setImageDrawable(ResourcesCompat.getDrawable(itemView.resources, R.drawable.ic_favorite_default, null))
            }
        }
    }
}