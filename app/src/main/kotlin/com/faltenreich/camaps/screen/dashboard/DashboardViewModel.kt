package com.faltenreich.camaps.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.MainStateProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel : ViewModel() {

    private val mainState = MainStateProvider.state

    val state = mainState.map { mainState ->
        DashboardState.Content(
            serviceState = mainState.serviceState,
            log = mainState.log,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = DashboardState.Loading,
    )
}