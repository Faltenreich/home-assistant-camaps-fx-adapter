package com.faltenreich.camaps.homeassistant

sealed interface HomeAssistantState {

    data object Idle : HomeAssistantState

    data object Disconnected : HomeAssistantState

    data class ConnectedDevice(val message: String) : HomeAssistantState

    data class ConnectedSensor(val message: String) : HomeAssistantState

    data class UpdatedSensor(val data: HomeAssistantData) : HomeAssistantState

    data class Error(val message: String) : HomeAssistantState
}