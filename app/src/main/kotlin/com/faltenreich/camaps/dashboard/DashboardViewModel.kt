package com.faltenreich.camaps.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.StateHolder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel : ViewModel() {

    private val camApsFxState = StateHolder.camApsFxState
    private val homeAssistantState = StateHolder.homeAssistantState
    private val log = StateHolder.log

    val state = combine(
        camApsFxState,
        homeAssistantState,
        log,
        ::DashboardState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = DashboardState(
            camApsFx = camApsFxState.value,
            homeAssistant = homeAssistantState.value,
            log = log.value,
        )
    )
}