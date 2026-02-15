package com.faltenreich.camaps.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SettingsRepository(private val keyValueStore: KeyValueStore) {

    fun getSettings(): Flow<Settings> = combine(
        keyValueStore.getString(KEY_HOME_ASSISTANT_URI),
        keyValueStore.getString(KEY_HOME_ASSISTANT_TOKEN),
        keyValueStore.getString(KEY_HOME_ASSISTANT_WEBHOOK_ID),
        keyValueStore.getStringSet(KEY_REGISTERED_SENSOR_UNIQUE_IDS),
    ) { uri, token, webhookId, registeredSensorUniqueIds ->
        Settings(
            homeAssistant = if (uri.isNullOrBlank() || token.isNullOrBlank()) {
                null
            } else {
                Settings.HomeAssistant(
                    uri = uri,
                    token = token,
                    webhookId = webhookId,
                    registeredSensorUniqueIds = registeredSensorUniqueIds ?: emptySet(),
                )
            },
        )
    }

    fun getDeviceId(): String {
        return keyValueStore.deviceId
    }

    suspend fun saveHomeAssistantUri(uri: String) {
        keyValueStore.putString(KEY_HOME_ASSISTANT_URI, uri)
    }

    suspend fun saveHomeAssistantToken(token: String) {
        keyValueStore.putString(KEY_HOME_ASSISTANT_TOKEN, token)
    }

    suspend fun saveHomeAssistantWebhookId(webhookId: String) {
        keyValueStore.putString(KEY_HOME_ASSISTANT_WEBHOOK_ID, webhookId)
    }

    suspend fun saveRegisteredSensorUniqueIds(sensorUniqueIds: Set<String>) {
        keyValueStore.putStringSet(KEY_REGISTERED_SENSOR_UNIQUE_IDS, sensorUniqueIds)
    }

    suspend fun clearRegisteredSensorUniqueIds() {
        keyValueStore.putStringSet(KEY_REGISTERED_SENSOR_UNIQUE_IDS, emptySet())
    }

    companion object {

        private const val KEY_HOME_ASSISTANT_URI = "home_assistant_uri"
        private const val KEY_HOME_ASSISTANT_TOKEN = "home_assistant_token"
        private const val KEY_HOME_ASSISTANT_WEBHOOK_ID = "home_assistant_webhook_id"
        private const val KEY_REGISTERED_SENSOR_UNIQUE_IDS = "registered_sensor_unique_ids"
    }
}