package com.faltenreich.camaps.homeassistant

sealed interface HomeAssistantState {

    data object Disconnected : HomeAssistantState

    sealed interface ConnectedDevice : HomeAssistantState

    sealed interface ConnectedSensor : HomeAssistantState
}