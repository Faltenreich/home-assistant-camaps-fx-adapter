package com.faltenreich.camaps.screen.login

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class LoginViewModel(
    private val repository: SettingsRepository = ServiceLocator.settingsRepository,
) : ViewModel() {

    var uri by mutableStateOf("")
    var token by mutableStateOf("")
    private val connection = MutableStateFlow<LoginState.Connection>(LoginState.Connection.Idle)

    val state = combine(
        snapshotFlow { uri },
        snapshotFlow { token },
        connection,
        ::LoginState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LoginState(
            uri = "",
            token = "",
            connection = LoginState.Connection.Idle,
        )
    )

    init {
        viewModelScope.launch {
            uri = repository.getHomeAssistantUri().firstOrNull() ?: ""
            token = repository.getHomeAssistantToken().firstOrNull() ?: ""
        }
        viewModelScope.launch {
            snapshotFlow { uri + token }
                .debounce(1.seconds)
                .distinctUntilChanged()
                .collect {
                    if (uri.isNotBlank() && token.isNotBlank()) {
                        pingHomeAssistant() }
                    }
        }
    }

    fun confirm() = viewModelScope.launch {
        repository.saveHomeAssistantUri(uri)
        repository.saveHomeAssistantToken(token)
    }

    private suspend fun pingHomeAssistant() {
        connection.update { LoginState.Connection.Loading }
        try {
            Log.d(TAG, "Testing connection to $uri")
            val client = HomeAssistantClient(host = uri, token = token)
            client.ping()
            connection.update { LoginState.Connection.Success }
            Log.d(TAG, "Connection successful")
        } catch (exception: Exception) {
            val errorMessage = when (exception) {
                is ResponseException -> exception.response.status.value.toString()
                else -> exception.message ?: "Unknown error"
            }
            Log.e(TAG, "Connection failed: $errorMessage", exception)
            connection.update { LoginState.Connection.Failure(errorMessage) }
        }
    }

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }
}