package com.faltenreich.camaps

import com.faltenreich.camaps.screen.dashboard.log.LogEntryFactory
import com.faltenreich.camaps.service.MainServiceState
import com.faltenreich.camaps.service.camaps.CamApsFxEvent
import com.faltenreich.camaps.service.homeassistant.HomeAssistantState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppStateProvider {

    private val _camApsEvent = MutableSharedFlow<CamApsFxEvent>()
    val camApsFxEvent = _camApsEvent.asSharedFlow()

    private val _state = MutableStateFlow(
        AppState(
            log = emptyList(),
        )
    )
    val state = _state.asStateFlow()

    fun setServiceState(serviceState: MainServiceState) {
        _state.update { it.copy(log = it.log + LogEntryFactory.create(serviceState)) }
    }

    suspend fun postEvent(event: CamApsFxEvent) {
        _state.update { state -> state.copy(log = state.log + LogEntryFactory.create(event)) }
        _camApsEvent.emit(event)
    }

    fun setHomeAssistantState(homeAssistantState: HomeAssistantState) {
        _state.update { it.copy(log = it.log + LogEntryFactory.create(homeAssistantState)) }
    }
}