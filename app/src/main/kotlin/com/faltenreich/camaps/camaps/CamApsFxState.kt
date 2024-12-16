package com.faltenreich.camaps.camaps

sealed interface CamApsFxState {

    data object None : CamApsFxState

    data class Value(val bloodSugar: BloodSugar) : CamApsFxState

    data class Error(val message: String) : CamApsFxState
}