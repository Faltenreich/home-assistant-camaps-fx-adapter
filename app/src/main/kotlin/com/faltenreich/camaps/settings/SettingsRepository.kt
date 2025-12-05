package com.faltenreich.camaps.settings

import android.content.Context
import android.content.SharedPreferences

class SettingsRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    fun getHomeAssistantUri(): String {
        return sharedPreferences.getString(KEY_HOME_ASSISTANT_URI, "http://homeassistant.local:8026") ?: ""
    }

    fun saveHomeAssistantUri(uri: String) {
        sharedPreferences.edit().putString(KEY_HOME_ASSISTANT_URI, uri).apply()
    }

    fun getHomeAssistantToken(): String {
        return sharedPreferences.getString(KEY_HOME_ASSISTANT_TOKEN, "") ?: ""
    }

    fun saveHomeAssistantToken(token: String) {
        sharedPreferences.edit().putString(KEY_HOME_ASSISTANT_TOKEN, token).apply()
    }

    fun getHomeAssistantWebhookId(): String {
        return sharedPreferences.getString(KEY_HOME_ASSISTANT_WEBHOOK_ID, "") ?: ""
    }

    fun saveHomeAssistantWebhookId(webhookId: String) {
        sharedPreferences.edit().putString(KEY_HOME_ASSISTANT_WEBHOOK_ID, webhookId).apply()
    }

    fun getRegisteredSensorUniqueIds(): MutableSet<String> {
        return sharedPreferences.getStringSet(KEY_REGISTERED_SENSOR_UNIQUE_IDS, emptySet()) ?: mutableSetOf()
    }

    fun saveRegisteredSensorUniqueIds(sensorUniqueIds: Set<String>) {
        sharedPreferences.edit().putStringSet(KEY_REGISTERED_SENSOR_UNIQUE_IDS, sensorUniqueIds).apply()
    }

    fun clearRegisteredSensorUniqueIds() {
        sharedPreferences.edit().remove(KEY_REGISTERED_SENSOR_UNIQUE_IDS).apply()
    }

    fun getUnitType(): String {
        return sharedPreferences.getString(KEY_UNIT_TYPE, "mmol/L") ?: "mmol/L"
    }

    fun saveUnitType(unitType: String) {
        sharedPreferences.edit().putString(KEY_UNIT_TYPE, unitType).apply()
    }

    companion object {
        private const val KEY_HOME_ASSISTANT_URI = "home_assistant_uri"
        private const val KEY_HOME_ASSISTANT_TOKEN = "home_assistant_token"
        private const val KEY_HOME_ASSISTANT_WEBHOOK_ID = "home_assistant_webhook_id"
        private const val KEY_REGISTERED_SENSOR_UNIQUE_IDS = "registered_sensor_unique_ids"
        private const val KEY_UNIT_TYPE = "unit_type"
    }
}