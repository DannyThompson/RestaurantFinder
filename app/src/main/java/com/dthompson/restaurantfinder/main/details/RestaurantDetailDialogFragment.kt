package com.dthompson.restaurantfinder.main.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.dthompson.core.Restaurant
import com.dthompson.restaurantfinder.R
import com.dthompson.restaurantfinder.util.StringUtil

/**
 * Dialog fragment used to display a more detailed view of restaurant details.
 * Utilizes ViewPager2 to allow the user to swipe through a set of images, if available.
 */
class RestaurantDetailDialogFragment(private val restaurant: Restaurant): AppCompatDialogFragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: RestaurantDetailAdapter
    private lateinit var textViewName: TextView
    private lateinit var textViewRatingValue: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var textViewRatingsCount: TextView
    private lateinit var textViewPrice: TextView
    private lateinit var textViewHours: TextView
    private lateinit var textViewAddress: TextView
    private lateinit var textViewPhone: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_detail_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!restaurant.photoReferences.isNullOrEmpty()) {
            adapter = RestaurantDetailAdapter(restaurant.photoReferences!!)
            viewPager = view.findViewById(R.id.view_pager)
            viewPager.adapter = adapter

            textViewName = view.findViewById(R.id.text_view_name)
            textViewName.text = restaurant.name

            textViewRatingValue = view.findViewById(R.id.text_view_rating_value)
            textViewRatingValue.text = restaurant.rating.toString()

            ratingBar = view.findViewById(R.id.rating_bar)
            ratingBar.rating = restaurant.rating

            textViewRatingsCount = view.findViewById(R.id.text_view_ratings)
            textViewRatingsCount.text = String.format(requireContext().getString(R.string.restaurant_total_ratings),
                restaurant.ratingCount)

            textViewPrice = view.findViewById(R.id.text_view_price)
            val price = restaurant.priceLevel
            val pluralsRes = if (price <= 2) R.plurals.price_cheap else R.plurals.price_expensive
            textViewPrice.text = StringUtil.getPriceString(restaurant.priceLevel, pluralsRes, view.context)

            textViewHours = view.findViewById(R.id.text_view_hours)
            if (restaurant.isOpen) {
                textViewHours.text = requireContext().getString(R.string.open)
                textViewHours.setTextColor(requireContext().getColor(R.color.quantum_googgreen))
            } else {
                textViewHours.text = requireContext().getString(R.string.closed)
                textViewHours.setTextColor(requireContext().getColor(R.color.quantum_googred))
            }

            textViewAddress = view.findViewById(R.id.text_view_address)
            textViewAddress.text = restaurant.address

            textViewPhone = view.findViewById(R.id.text_view_phone)
            if (restaurant.phone != null) {
                textViewPhone.text = restaurant.phone
            } else {
                textViewPhone.visibility = View.GONE
            }
        }
    }
}