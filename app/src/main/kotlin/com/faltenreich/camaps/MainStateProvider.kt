package com.faltenreich.camaps

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.dashboard.log.LogEntry
import com.faltenreich.camaps.dashboard.log.LogEntryFactory
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object MainStateProvider {

    private const val MAX_LOG_ENTRIES = 100

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun setServiceState(serviceState: MainServiceState) {
        val logEntry = LogEntryFactory.create(serviceState)
        _state.update { it.copy(serviceState = serviceState, log = (it.log + logEntry).takeLast(MAX_LOG_ENTRIES)) }
    }

    fun setCamApsFxState(camApsFxState: CamApsFxState) {
        LogEntryFactory.create(camApsFxState)?.let { logEntry ->
            _state.update { it.copy(camApsFxState = camApsFxState, log = (it.log + logEntry).takeLast(MAX_LOG_ENTRIES)) }
        }
    }

    fun setHomeAssistantState(homeAssistantState: HomeAssistantState) {
        val logEntry = LogEntryFactory.create(homeAssistantState)
        _state.update {
            it.copy(
                homeAssistantState = homeAssistantState,
                log = (it.log + logEntry).takeLast(MAX_LOG_ENTRIES),
            )
        }
    }

    fun addLog(message: String) {
        val logEntry = LogEntryFactory.create(message)
        _state.update { it.copy(log = (it.log + logEntry).takeLast(MAX_LOG_ENTRIES)) }
    }
}