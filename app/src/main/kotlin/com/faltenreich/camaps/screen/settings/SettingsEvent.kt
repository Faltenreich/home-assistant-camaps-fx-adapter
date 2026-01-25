package com.faltenreich.camaps.screen.settings

sealed interface SettingsEvent {

    data object UpdatedSuccessfully : SettingsEvent
}