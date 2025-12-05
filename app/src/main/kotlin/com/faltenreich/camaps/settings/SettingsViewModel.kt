package com.faltenreich.camaps.settings

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.ReinitializationManager
import com.faltenreich.camaps.homeassistant.network.HomeAssistantClient
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)

    private val _uri = MutableStateFlow(settingsRepository.getHomeAssistantUri())
    val uri = _uri.asStateFlow()

    private val _token = MutableStateFlow(settingsRepository.getHomeAssistantToken())
    val token = _token.asStateFlow()

    private val _unitType = MutableStateFlow(settingsRepository.getUnitType())
    val unitType = _unitType.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    val connectionState = _connectionState.asStateFlow()

    fun onUriChanged(uri: String) {
        _uri.value = uri
        settingsRepository.saveHomeAssistantUri(uri)
        _connectionState.value = ConnectionState.Idle // Reset on change
    }

    fun onTokenChanged(token: String) {
        _token.value = token
        settingsRepository.saveHomeAssistantToken(token)
        _connectionState.value = ConnectionState.Idle // Reset on change
    }

    fun onUnitTypeChanged(unitType: String) {
        _unitType.value = unitType
        settingsRepository.saveUnitType(unitType)
    }

    fun testConnection() {
        viewModelScope.launch {
            _connectionState.value = ConnectionState.Loading
            try {
                Log.d(TAG, "Testing connection to ${uri.value} with token ${token.value.take(4)}...")
                val client = HomeAssistantClient.getInstance(
                    host = uri.value,
                    token = token.value
                )
                client.testConnection()
                _connectionState.value = ConnectionState.Success
                ReinitializationManager.reinitialize()
                Log.d(TAG, "Connection successful")
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is ResponseException -> e.response.status.value.toString()
                    else -> e.message ?: "Unknown error"
                }
                _connectionState.value = ConnectionState.Failure(errorMessage)
                Log.e(TAG, "Connection failed: $errorMessage", e)
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

    companion object {
        private val TAG = SettingsViewModel::class.java.simpleName
    }
}

sealed class ConnectionState {
    object Idle : ConnectionState()
    object Loading : ConnectionState()
    object Success : ConnectionState()
    data class Failure(val message: String) : ConnectionState()
}
