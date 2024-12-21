package com.faltenreich.camaps

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.dashboard.log.LogEntryFactory
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object MainStateProvider {

    private val _state = MutableStateFlow<MainState>(
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
        _state.update { state ->
            state.copy(
                camApsFxState = camApsFxState,
                log = LogEntryFactory.create(camApsFxState)?.let { state.log + it } ?: state.log,
            )
        }
    }

    fun setHomeAssistantState(homeAssistantState: HomeAssistantState) {
        _state.update { state ->
            state.copy(
                homeAssistantState = homeAssistantState,
                log = state.log + LogEntryFactory.create(homeAssistantState),
            )
        }
    }
}