package com.faltenreich.camaps.screen.settings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.ServiceLocator
import com.faltenreich.camaps.service.homeassistant.network.HomeAssistantClient
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SettingsViewModel(
    private val repository: SettingsRepository = ServiceLocator.settingsRepository,
) : ViewModel() {

    var uri by mutableStateOf("")
    var token by mutableStateOf("")
    private val connection = MutableStateFlow<SettingsState.Connection>(SettingsState.Connection.Loading)

    val state = combine(
        snapshotFlow { uri },
        snapshotFlow { token },
        connection,
        ::SettingsState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = SettingsState(
            uri = "",
            token = "",
            connection = SettingsState.Connection.Loading,
        )
    )

    private val _event = MutableSharedFlow<SettingsEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            uri = repository.getHomeAssistantUri().firstOrNull() ?: ""
            token = repository.getHomeAssistantToken().firstOrNull() ?: ""
        }
        viewModelScope.launch {
            snapshotFlow { uri + token }
                .debounce(1.seconds)
                .distinctUntilChanged()
                .collect { pingHomeAssistant() }
        }
    }

    fun confirm() = viewModelScope.launch {
        _event.emit(SettingsEvent.UpdatedSuccessfully)
        repository.saveHomeAssistantUri(uri)
        repository.saveHomeAssistantToken(token)
    }

    private suspend fun pingHomeAssistant() {
        connection.update { SettingsState.Connection.Loading }
        try {
            Log.d(TAG, "Testing connection to $uri")
            val client = HomeAssistantClient(host = uri, token = token)
            client.ping()
            connection.update { SettingsState.Connection.Success }
            Log.d(TAG, "Connection successful")
        } catch (exception: Exception) {
            val errorMessage = when (exception) {
                is ResponseException -> exception.response.status.value.toString()
                else -> exception.message ?: "Unknown error"
            }
            Log.e(TAG, "Connection failed: $errorMessage", exception)
            connection.update { SettingsState.Connection.Failure(errorMessage) }
        }
    }

    companion object {
        private val TAG = SettingsViewModel::class.java.simpleName
    }
}