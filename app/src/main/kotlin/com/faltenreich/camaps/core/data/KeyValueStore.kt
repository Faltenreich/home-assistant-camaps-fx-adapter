package com.faltenreich.camaps.core.data

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import androidx.core.content.edit

object KeyValueStore {

    private lateinit var sharedPreferences: SharedPreferences

    // TODO: Remove (from here)
    lateinit var deviceId: String

    fun setup(context: Context) {
        sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }

    fun getString(key: String, default: String? = null): String? {
        return sharedPreferences.getString(key, default)
    }

    fun putStringSet(key: String, value: Set<String>) {
        sharedPreferences.edit { putStringSet(key, value) }
    }

    fun getStringSet(key: String, default: Set<String>? = null): Set<String>? {
        return sharedPreferences.getStringSet(key, default)
    }
}