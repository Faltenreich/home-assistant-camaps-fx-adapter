package com.faltenreich.camaps.camaps

import com.faltenreich.camaps.BloodSugar

sealed interface CamApsFxState {

    data object None : CamApsFxState

    data class Value(val bloodSugar: BloodSugar) : CamApsFxState

    data class Error(val message: String) : CamApsFxState
}