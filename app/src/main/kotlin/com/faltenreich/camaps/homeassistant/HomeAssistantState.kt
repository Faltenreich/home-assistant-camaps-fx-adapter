package com.faltenreich.camaps.homeassistant

sealed interface HomeAssistantState {

    data object Disconnected : HomeAssistantState

    data object ConnectedDevice : HomeAssistantState

    data object ConnectedSensor : HomeAssistantState

    data class Error(val message: String) : HomeAssistantState
}