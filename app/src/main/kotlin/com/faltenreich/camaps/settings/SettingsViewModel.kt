package com.faltenreich.camaps.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.homeassistant.network.HomeAssistantClient
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SettingsViewModel : ViewModel() {

    private val repository = SettingsRepository

    private val _state = MutableStateFlow(
        SettingsState(
            uri = repository.getHomeAssistantUri() ?: "",
            token = repository.getHomeAssistantToken() ?: "",
            connection = SettingsState.Connection.Loading,
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state
                .debounce(1.seconds)
                .distinctUntilChanged { old, new -> old.uri == new.uri && old.token == new.token }
                .collect { update -> pingHomeAssistant(update) }
        }
    }

    fun update(state: SettingsState) {
        _state.update { state }
        repository.saveHomeAssistantUri(state.uri)
        repository.saveHomeAssistantToken(state.token)
    }

    private suspend fun pingHomeAssistant(state: SettingsState) {
        _state.update { state.copy(connection = SettingsState.Connection.Loading) }
        try {
            Log.d(TAG, "Testing connection to ${state.uri}")
            val client = HomeAssistantClient(host = state.uri, token = state.token)
            client.ping()
            _state.update { state -> state.copy(connection = SettingsState.Connection.Success) }
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

    companion object {
        private val TAG = SettingsViewModel::class.java.simpleName
    }
}