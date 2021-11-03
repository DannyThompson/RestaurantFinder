package com.dthompson.services.storage

interface LocalStorage {
    fun addFavoriteId(placeId: String)

    fun removeFavoriteId(placeId: String)

    fun getFavorites(): Set<String>
}