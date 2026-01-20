package com.faltenreich.camaps.settings

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.homeassistant.network.HomeAssistantClient
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)

    private val _state = MutableStateFlow(
        SettingsState(
            uri = settingsRepository.getHomeAssistantUri(),
            token = settingsRepository.getHomeAssistantToken(),
            notificationTimeoutMinutes = settingsRepository.getNotificationTimeoutMinutes(),
            connection = SettingsState.Connection.Idle,
            hasPermission = false,
        )
    )
    val state = _state.asStateFlow()

    fun onUriChanged(uri: String) {
        settingsRepository.saveHomeAssistantUri(uri)
        _state.update { state ->
            state.copy(
                uri = uri,
                connection = SettingsState.Connection.Idle,
            )
        }
    }

    fun onTokenChanged(token: String) {
        settingsRepository.saveHomeAssistantToken(token)
        _state.update { state ->
            state.copy(
                token = token,
                connection = SettingsState.Connection.Idle,
            )
        }
    }

    fun onNotificationTimeoutMinutesChanged(minutes: String) {
        val minutesAsNumber = minutes.toIntOrNull()?.coerceAtLeast(0) ?: 0
        settingsRepository.saveNotificationTimeoutMinutes(minutesAsNumber)
        _state.update { state ->
            state.copy(
                notificationTimeoutMinutes = minutesAsNumber,
            )
        }
    }

    fun checkPermission(context: Context) {
        val componentName = ComponentName(context, "com.faltenreich.camaps.MainService")
        val enabledListeners = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        _state.update { state ->
            state.copy(
                hasPermission = enabledListeners?.contains(componentName.flattenToString()) == true,
            )
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            val state = _state.value
            _state.update { state.copy(connection = SettingsState.Connection.Loading) }
            try {
                Log.d(TAG, "Testing connection to ${state.uri} with token ${state.token.take(4)}...")
                val client = HomeAssistantClient.getInstance(
                    host = state.uri,
                    token = state.token,
                )
                client.testConnection()
                _state.update { state -> state.copy(connection = SettingsState.Connection.Success) }
                ReinitializationManager.reinitialize()
                Log.d(TAG, "Connection successful")
            } catch (exception: Exception) {
                val errorMessage = when (exception) {
                    is ResponseException -> exception.response.status.value.toString()
                    else -> exception.message ?: "Unknown error"
                }
                Log.e(TAG, "Connection failed: $errorMessage", exception)
                _state.update { state -> state.copy(connection = SettingsState.Connection.Failure(errorMessage)) }
            }
        }
    }

    fun openNotificationSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        activity.startActivityForResult(intent, 1001)
    }

    fun restartService() {
        Log.d(TAG, "Triggering Home Assistant re-initialization")
        val context = getApplication<Application>().applicationContext
        Toast.makeText(context, "Re-initializing Home Assistant connection...", Toast.LENGTH_SHORT).show()
        viewModelScope.launch {
            ReinitializationManager.reinitialize()
        }
    }

    fun reset() {
        Log.d(TAG, "Resetting Home Assistant registration")
        val context = getApplication<Application>().applicationContext
        settingsRepository.saveHomeAssistantWebhookId("")
        settingsRepository.clearRegisteredSensorUniqueIds()
        Toast.makeText(context, "Home Assistant registration has been reset", Toast.LENGTH_SHORT).show()
        viewModelScope.launch {
            ReinitializationManager.reinitialize()
        }
    }

    companion object {
        private val TAG = SettingsViewModel::class.java.simpleName
    }
}