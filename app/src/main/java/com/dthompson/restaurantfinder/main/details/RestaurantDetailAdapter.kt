package com.dthompson.restaurantfinder.main.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dthompson.core.PHOTOS_URL
import com.dthompson.restaurantfinder.BuildConfig
import com.dthompson.restaurantfinder.R

/**
 * Simple adapter for our ViewPager to show a restaurant's photos.
 */
class RestaurantDetailAdapter(private val photoUrls: List<String>): RecyclerView.Adapter<RestaurantDetailAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_item_image, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUrl = PHOTOS_URL.replace("{reference}", photoUrls[position])
            .replace("{key}", BuildConfig.GOOGLE_PLACES_API_KEY)
        holder.imageView.load(photoUrl)
    }

    override fun getItemCount(): Int {
        return photoUrls.size
    }

    class PhotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.image_view_detail_photo)
    }
}