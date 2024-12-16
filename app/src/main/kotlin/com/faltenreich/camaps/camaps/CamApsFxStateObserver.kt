package com.faltenreich.camaps.camaps

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object CamApsFxStateObserver {

    private val _state = MutableStateFlow<CamApsFxState>(CamApsFxState.None)
    val state = _state.asStateFlow()

    fun setState(state: CamApsFxState) {
        _state.tryEmit(state)
    }
}