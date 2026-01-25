package com.faltenreich.camaps.camaps

sealed interface CamApsFxState {

    data object Blank : CamApsFxState
    data object Off : CamApsFxState
    data object Starting : CamApsFxState

    data class BloodSugar(
        val value: Float,
        val unitOfMeasurement: String,
    ) : CamApsFxState

    data class Error(val message: String) : CamApsFxState
}