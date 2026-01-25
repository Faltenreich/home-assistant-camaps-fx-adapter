package com.faltenreich.camaps.homeassistant

sealed interface HomeAssistantData {

    data class BloodSugar(
        val value: Float,
        val unitOfMeasurement: String,
    ) : HomeAssistantData
}