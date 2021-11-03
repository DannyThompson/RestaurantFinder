package com.dthompson.services.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

private const val PREF_FAVORITED_IDS = "PREF_FAVORITED_IDS"
class DefaultLocalStorage(context: Context): LocalStorage {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun addFavoriteId(placeId: String) {
        val currentFavorites = mutableSetOf<String>()
        currentFavorites.addAll(sharedPreferences.getStringSet(PREF_FAVORITED_IDS, mutableSetOf())!!.toMutableSet())
        currentFavorites.add(placeId)
        sharedPreferences.edit()
            .putStringSet(PREF_FAVORITED_IDS, currentFavorites)
            .apply()
    }

    override fun removeFavoriteId(placeId: String) {
        val currentFavorites = mutableSetOf<String>()
        currentFavorites.addAll(sharedPreferences.getStringSet(PREF_FAVORITED_IDS, mutableSetOf())!!.toMutableSet())
        currentFavorites.remove(placeId)
        sharedPreferences.edit()
            .putStringSet(PREF_FAVORITED_IDS, currentFavorites)
            .apply()
    }

    override fun getFavorites(): Set<String> {
        return sharedPreferences.getStringSet(PREF_FAVORITED_IDS, mutableSetOf())!!
    }
}