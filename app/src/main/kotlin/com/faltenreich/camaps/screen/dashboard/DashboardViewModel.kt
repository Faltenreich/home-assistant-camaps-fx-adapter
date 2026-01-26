package com.faltenreich.camaps.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.AppStateProvider
import com.faltenreich.camaps.ServiceLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    appStateProvider: AppStateProvider = ServiceLocator.appStateProvider,
) : ViewModel() {

    private val appState = appStateProvider.state

    val state = appState.map { DashboardState.Content(log = it.log) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = DashboardState.Loading,
    )
}