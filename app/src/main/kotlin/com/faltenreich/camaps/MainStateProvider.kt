package com.faltenreich.camaps

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.dashboard.log.LogEntryFactory
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object MainStateProvider {

    private const val MAX_LOG_ENTRIES = 200

    private val _state = MutableStateFlow(
        MainState(
            serviceState = MainServiceState.Disconnected,
            camApsFxState = CamApsFxState.Blank,
            homeAssistantState = HomeAssistantState.Disconnected,
            log = emptyList(),
        )
    )
    val state = _state.asStateFlow()

    fun setServiceState(serviceState: MainServiceState) {
        _state.update { state ->
            state.copy(
                serviceState = serviceState,
                log = state.log + LogEntryFactory.create(serviceState),
            )
        }
    }

    fun setCamApsFxState(camApsFxState: CamApsFxState) {
        val logEntry = LogEntryFactory.create(camApsFxState) ?: return;
        _state.update { state ->
            state.copy(
                camApsFxState = camApsFxState,
                log = state.log + logEntry,
            )
        }
    }

    fun setHomeAssistantState(homeAssistantState: HomeAssistantState) {
        val logEntry = LogEntryFactory.create(homeAssistantState)
        _state.update { state ->
            state.copy(
                homeAssistantState = homeAssistantState,
                log = (state.log + logEntry).takeLast(MAX_LOG_ENTRIES),
            )
        }
    }

    fun addLog(message: String) {
        val logEntry = LogEntryFactory.create(message)
        _state.update { state ->
            state.copy(
                log = state.log + logEntry,
            )
        }
    }
}
