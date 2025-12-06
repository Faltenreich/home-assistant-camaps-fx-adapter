package com.faltenreich.camaps.homeassistant

import com.faltenreich.camaps.camaps.CamApsFxState

sealed interface HomeAssistantData {

    data class BloodSugar(
        val value: Float,
        val unitOfMeasurement: String,
        val trend: CamApsFxState.BloodSugar.Trend?
    ) : HomeAssistantData
}