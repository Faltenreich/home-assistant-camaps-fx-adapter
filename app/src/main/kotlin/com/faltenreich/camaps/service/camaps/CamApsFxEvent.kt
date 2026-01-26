package com.faltenreich.camaps.service.camaps

sealed interface CamApsFxEvent {

    data class BloodSugar(
        val value: Float,
        val unitOfMeasurement: String,
    ) : CamApsFxEvent

    data class Unknown(
        val message: String,
    ) : CamApsFxEvent
}