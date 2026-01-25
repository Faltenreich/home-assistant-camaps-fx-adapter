package com.faltenreich.camaps.service.homeassistant

sealed interface HomeAssistantData {

    data class BloodSugar(
        val value: Float,
        val unitOfMeasurement: String,
    ) : HomeAssistantData
}