package com.faltenreich.camaps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.screen.login.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    settingsRepository: SettingsRepository = ServiceLocator.settingsRepository,
) : ViewModel() {

    val state = combine(
        settingsRepository.getHomeAssistantUri(),
        settingsRepository.getHomeAssistantToken(),
    ) { (uri, token) ->
        if (uri?.isNotBlank() == true && token?.isNotBlank() == true) MainState.Authenticated
        else MainState.Unauthenticated
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = MainState.Loading,
    )
}