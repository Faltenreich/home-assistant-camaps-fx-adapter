package com.faltenreich.camaps.homeassistant

sealed interface HomeAssistantData {

    data class BloodSugar(val mmolL: Float) : HomeAssistantData
}