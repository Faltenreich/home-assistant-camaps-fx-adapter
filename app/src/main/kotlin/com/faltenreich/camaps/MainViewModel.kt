package com.faltenreich.camaps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.core.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    settingsRepository: SettingsRepository = locate(),
) : ViewModel() {

    val state = settingsRepository.getSettings().map { settings ->
        if (settings.homeAssistant != null) MainState.Authenticated
        else MainState.Unauthenticated
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = MainState.Loading,
    )
}