package com.faltenreich.camaps

import com.faltenreich.camaps.screen.dashboard.log.LogEntry
import com.faltenreich.camaps.screen.dashboard.log.LogEntryFactory
import com.faltenreich.camaps.service.camaps.CamApsFxEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppStateProvider {

    private val _camApsEvent = MutableSharedFlow<CamApsFxEvent>()
    val camApsFxEvent = _camApsEvent.asSharedFlow()

    private val _log = MutableStateFlow<List<LogEntry>>(emptyList())
    val log = _log.asStateFlow()

    suspend fun postEvent(event: CamApsFxEvent) {
        _camApsEvent.emit(event)
        _log.update { it + LogEntryFactory.create(event) }
    }

    fun addLog(logEntry: LogEntry) {
        _log.update { it + logEntry }
    }
}