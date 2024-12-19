package com.faltenreich.camaps.homeassistant

sealed interface HomeAssistantData {

    data class BloodSugar(val mgDl: Float) : HomeAssistantData
}