package com.faltenreich.camaps.core.data

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SuppressLint("StaticFieldLeak")
object KeyValueStore {

    private lateinit var context: Context
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    // TODO: Remove (from here)
    lateinit var deviceId: String

    fun setup(context: Context) {
        this.context = context
        deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    suspend fun putString(key: String, value: String) {
        context.dataStore.updateData { data ->
            data.toMutablePreferences().also { preferences ->
                preferences[stringPreferencesKey(key)] = value
            }
        }
    }

    fun getString(key: String, default: String? = null): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: default
        }
    }

    suspend fun putStringSet(key: String, value: Set<String>) {
        context.dataStore.updateData { data ->
            data.toMutablePreferences().also { preferences ->
                preferences[stringSetPreferencesKey(key)] = value
            }
        }
    }

    fun getStringSet(key: String, default: Set<String>? = null): Flow<Set<String>?> {
        return context.dataStore.data.map { preferences ->
            preferences[stringSetPreferencesKey(key)] ?: default
        }
    }
}