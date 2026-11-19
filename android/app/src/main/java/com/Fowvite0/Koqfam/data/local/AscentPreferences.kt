package com.Fowvite0.Koqfam.data.local

import android.content.Context
import android.content.SharedPreferences

class AscentPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int = 0): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean = false): Boolean =
        prefs.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun readString(key: String): String? = prefs.getString(key, null)

    fun writeString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    companion object {
        private const val STORE = "olympus_thunderbound"

        const val KEY_BEST_HEIGHT = "best_height"
        const val KEY_RELICS_TOTAL = "relics_total"
        const val KEY_FLOORS_CLEARED = "floors_cleared"
        const val KEY_ASSIST_TELEGRAPH = "assist_telegraph"
        const val KEY_ASSIST_SWAP = "assist_swap_sides"
        const val KEY_EDITOR_DRAFT = "editor_draft"
        const val PREFIX_FLOOR_BEST = "floor_best_"
        const val PREFIX_FLOOR_RELICS = "floor_relics_"
        const val PREFIX_CUSTOM = "custom_level_"
    }
}
