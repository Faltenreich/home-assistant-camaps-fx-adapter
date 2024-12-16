package com.faltenreich.camaps

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object MainStateObserver {

    private val _state = MutableStateFlow<MainState>(MainState.Initial)
    val state = _state.asStateFlow()

    fun setCamApsState(state: CamApsFxState) {
        _state.update { _state.value.copy(camApsFxState = state) }
    }

    fun setHomeAssistantState(state: HomeAssistantState) {
        _state.update { _state.value.copy(homeAssistantState = state) }
    }
}