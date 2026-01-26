package com.faltenreich.camaps.service.homeassistant

sealed interface HomeAssistantState {

    data object Disconnected : HomeAssistantState

    data object DeviceConnected : HomeAssistantState

    data object SensorConnected : HomeAssistantState

    data class SensorUpdated(val data: HomeAssistantData) : HomeAssistantState

    data class Error(val message: String) : HomeAssistantState
}