package com.faltenreich.camaps

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.dashboard.log.LogEntryFactory
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object MainStateProvider {

    private val _state = MutableStateFlow<MainState>(
        MainState(
            service = MainServiceState.Disconnected,
            camApsFx = CamApsFxState.Blank,
            homeAssistant = HomeAssistantState.Disconnected,
            log = emptyList(),
        )
    )
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MainEvent>(replay = 1)
    val event = _event.asSharedFlow()

    fun toggleService() {
        _event.tryEmit(MainEvent.ToggleService)
    }

    fun setServiceState(serviceState: MainServiceState) {
        _state.update { state ->
            state.copy(
                service = serviceState,
                log = state.log + LogEntryFactory.create(serviceState),
            )
        }
    }

    fun setCamApsFxState(camApsFxState: CamApsFxState) {
        _state.update { state ->
            state.copy(
                camApsFx = camApsFxState,
                log = LogEntryFactory.create(camApsFxState)?.let { state.log + it } ?: state.log,
            )
        }
    }

    fun setHomeAssistantState(homeAssistantState: HomeAssistantState) {
        _state.update { state ->
            state.copy(
                homeAssistant = homeAssistantState,
                log = state.log + LogEntryFactory.create(homeAssistantState),
            )
        }
    }
}