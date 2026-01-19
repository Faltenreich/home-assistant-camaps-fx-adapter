package com.faltenreich.camaps

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.dashboard.log.LogEntry
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
        val logEntry = LogEntryFactory.create(serviceState)
        _state.update { state ->
            state.copy(
                serviceState = serviceState,
                log = state.log.addEntry(logEntry)
            )
        }
    }

    fun setCamApsFxState(camApsFxState: CamApsFxState) {
        val logEntry = LogEntryFactory.create(camApsFxState)
        _state.update { state ->
            state.copy(
                camApsFxState = camApsFxState,
                log = state.log.addEntry(logEntry)
            )
        }
    }

    fun setHomeAssistantState(homeAssistantState: HomeAssistantState) {
        val logEntry = LogEntryFactory.create(homeAssistantState)
        _state.update { state ->
            state.copy(
                homeAssistantState = homeAssistantState,
                log = state.log.addEntry(logEntry)
            )
        }
    }

    fun addLog(message: String) {
        val logEntry = LogEntryFactory.create(message)
        _state.update { state ->
            state.copy(
                log = state.log.addEntry(logEntry)
            )
        }
    }

    private fun List<LogEntry>.addEntry(entry: LogEntry?): List<LogEntry> {
        return if (entry != null) (this + entry).takeLast(MAX_LOG_ENTRIES) else this
    }
}