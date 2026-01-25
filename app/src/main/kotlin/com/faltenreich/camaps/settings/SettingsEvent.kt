package com.faltenreich.camaps.settings

sealed interface SettingsEvent {

    data object UpdatedSuccessfully : SettingsEvent
}