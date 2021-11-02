package com.dthompson.core

const val BASE_URL = "https://maps.googleapis.com/maps/"
const val GOOGLE_PLACES_API_KEY = "AIzaSyDQSd210wKX_7cz9MELkxhaEOUhFP0AkSk"
// For now, all we care about is restaurants, so just hard code the type.
const val REQUESTED_TYPE = "restaurant"
const val QUERY_TYPE_ALL = "QUERY_TYPE_ALL"
const val QUERY_TYPE_SEARCH = "QUERY_TYPE_SEARCH"
// Additional fields we want to request when a user clicks on a restaurant.
const val DETAILS_PHONE_AND_PHOTOS = "photos,formatted_phone_number"

// We always use the same URL to just call Coil.load() on an imageview to set a photo, so just hardcode the url and replace the necessary parameters when needed.
const val PHOTOS_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=350&maxheight=350&photo_reference={reference}&key={key}"
