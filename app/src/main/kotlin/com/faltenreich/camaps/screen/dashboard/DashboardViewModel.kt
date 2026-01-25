package com.faltenreich.camaps.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.MainStateProvider
import com.faltenreich.camaps.ServiceLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    mainStateProvider: MainStateProvider = ServiceLocator.mainStateProvider,
) : ViewModel() {

    private val mainState = mainStateProvider.state

    val state = mainState.map { DashboardState.Content(log = it.log) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = DashboardState.Loading,
    )
}