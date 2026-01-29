package com.faltenreich.camaps.screen.login

import com.faltenreich.camaps.core.data.KeyValueStore
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val keyValueStore: KeyValueStore) {

    fun getDeviceId(): String {
        return keyValueStore.deviceId
    }

    fun getHomeAssistantUri(): Flow<String?> {
        return keyValueStore.getString(KEY_HOME_ASSISTANT_URI, null)
    }

    suspend fun saveHomeAssistantUri(uri: String) {
        keyValueStore.putString(KEY_HOME_ASSISTANT_URI, uri)
    }

    fun getHomeAssistantToken(): Flow<String?> {
        return keyValueStore.getString(KEY_HOME_ASSISTANT_TOKEN)
    }

    suspend fun saveHomeAssistantToken(token: String) {
        keyValueStore.putString(KEY_HOME_ASSISTANT_TOKEN, token)
    }

    fun getHomeAssistantWebhookId(): Flow<String?> {
        return keyValueStore.getString(KEY_HOME_ASSISTANT_WEBHOOK_ID)
    }

    suspend fun saveHomeAssistantWebhookId(webhookId: String) {
        keyValueStore.putString(KEY_HOME_ASSISTANT_WEBHOOK_ID, webhookId)
    }

    fun getRegisteredSensorUniqueIds(): Flow<Set<String>?> {
        return keyValueStore.getStringSet(KEY_REGISTERED_SENSOR_UNIQUE_IDS)
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