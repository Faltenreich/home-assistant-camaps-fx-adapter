package com.faltenreich.camaps

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.dashboard.log.LogEntry
import com.faltenreich.camaps.dashboard.log.LogEntryFactory
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object StateHolder {

    private val logEntryFactory = LogEntryFactory()

    private val _camApsFxState = MutableStateFlow<CamApsFxState>(CamApsFxState.None)
    val camApsFxState = _camApsFxState.asStateFlow()

    private val _homeAssistantState = MutableStateFlow<HomeAssistantState>(HomeAssistantState.Disconnected)
    val homeAssistantState = _homeAssistantState.asStateFlow()

    private val _log = MutableStateFlow<List<LogEntry>>(emptyList())
    val log = _log.asStateFlow()

    fun setCamApsFxState(state: CamApsFxState) {
        _camApsFxState.update { state }

        logEntryFactory.create(state)?.let { logEntry ->
            _log.update { it + logEntry }
        }
    }

    fun setHomeAssistantState(state: HomeAssistantState) {
        _homeAssistantState.update { state }

        logEntryFactory.create(state)?.let { logEntry ->
            _log.update { it + logEntry }
        }
    }
}