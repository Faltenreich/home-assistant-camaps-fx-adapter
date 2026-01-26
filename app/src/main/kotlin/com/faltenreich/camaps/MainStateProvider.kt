package com.faltenreich.camaps

import com.faltenreich.camaps.screen.dashboard.log.LogEntryFactory
import com.faltenreich.camaps.service.MainServiceState
import com.faltenreich.camaps.service.camaps.CamApsFxState
import com.faltenreich.camaps.service.homeassistant.HomeAssistantState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainStateProvider {

    private val _state = MutableStateFlow(
        MainState(
            permission = MainState.Permission.Loading,
            camApsFxState = CamApsFxState.Blank,
            log = emptyList(),
        )
    )
    val state = _state.asStateFlow()

    fun setPermissionState(permissionState: MainState.Permission) {
        _state.update { state -> state.copy(permission = permissionState) }
    }

    fun setServiceState(serviceState: MainServiceState) {
        val logEntry = LogEntryFactory.create(serviceState)
        _state.update { it.copy(log = it.log + logEntry) }
    }

    fun setCamApsFxState(camApsFxState: CamApsFxState) {
        val logEntry = LogEntryFactory.create(camApsFxState)
        _state.update { state ->
            state.copy(
                camApsFxState = camApsFxState,
                log = logEntry?.let { state.log + logEntry } ?: state.log,
            )
        }
    }

    fun setHomeAssistantState(homeAssistantState: HomeAssistantState) {
        _state.update { it.copy(log = it.log + LogEntryFactory.create(homeAssistantState)) }
    }
}