package com.faltenreich.camaps.settings

object SettingsRepository {

    private const val KEY_HOME_ASSISTANT_URI = "home_assistant_uri"
    private const val KEY_HOME_ASSISTANT_TOKEN = "home_assistant_token"
    private const val KEY_HOME_ASSISTANT_WEBHOOK_ID = "home_assistant_webhook_id"
    private const val KEY_REGISTERED_SENSOR_UNIQUE_IDS = "registered_sensor_unique_ids"
    private const val KEY_NOTIFICATION_TIMEOUT_MINUTES = "notification_timeout_minutes"

    private const val VALUE_HOME_ASSISTANT_URI_DEFAULT = "http://homeassistant.local:8026"

    private val keyValueStore = KeyValueStore

    fun getHomeAssistantUri(): String? {
        return keyValueStore.getString(KEY_HOME_ASSISTANT_URI, VALUE_HOME_ASSISTANT_URI_DEFAULT)
    }

    fun saveHomeAssistantUri(uri: String) {
        keyValueStore.putString(KEY_HOME_ASSISTANT_URI, uri)
    }

    fun getHomeAssistantToken(): String? {
        return keyValueStore.getString(KEY_HOME_ASSISTANT_TOKEN)
    }

    fun saveHomeAssistantToken(token: String) {
        keyValueStore.putString(KEY_HOME_ASSISTANT_TOKEN, token)
    }

    fun getHomeAssistantWebhookId(): String? {
        return keyValueStore.getString(KEY_HOME_ASSISTANT_WEBHOOK_ID)
    }

    fun saveHomeAssistantWebhookId(webhookId: String) {
        keyValueStore.putString(KEY_HOME_ASSISTANT_WEBHOOK_ID, webhookId)
    }

    fun getRegisteredSensorUniqueIds(): Set<String> {
        return keyValueStore.getStringSet(KEY_REGISTERED_SENSOR_UNIQUE_IDS) ?: emptySet()
    }

    fun saveRegisteredSensorUniqueIds(sensorUniqueIds: Set<String>) {
        keyValueStore.putStringSet(KEY_REGISTERED_SENSOR_UNIQUE_IDS, sensorUniqueIds)
    }

    fun clearRegisteredSensorUniqueIds() {
        keyValueStore.putStringSet(KEY_REGISTERED_SENSOR_UNIQUE_IDS, emptySet())
    }

    fun getNotificationTimeoutMinutes(): Int? {
        return keyValueStore.getInt(KEY_NOTIFICATION_TIMEOUT_MINUTES).takeIf { it > 0 }
    }

    fun saveNotificationTimeoutMinutes(minutes: Int) {
        keyValueStore.putInt(KEY_NOTIFICATION_TIMEOUT_MINUTES, minutes)
    }
}