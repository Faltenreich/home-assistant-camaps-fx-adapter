package com.faltenreich.camaps.service.camaps

sealed interface CamApsFxState {

    data object Blank : CamApsFxState

    data class BloodSugar(
        val value: Float,
        val unitOfMeasurement: String,
    ) : CamApsFxState

    data class Error(val message: String) : CamApsFxState
}