package com.faltenreich.camaps.camaps

sealed interface CamApsFxState {

    data object None : CamApsFxState

    data class Value(val bloodSugar: BloodSugar) : CamApsFxState {

        data class BloodSugar(
            val mgDl: Float,
            val trend: Trend?,
        ) {

            enum class Trend(val imageResourceId: Int) {
                RISING_FAST(-1), // TODO
                RISING(-1), // TODO
                RISING_SLOW(-1), // TODO
                STEADY(2131230951),
                DROPPING_SLOW(2131230942),
                DROPPING(-1), // TODO
                DROPPING_FAST(-1), // TODO
            }
        }
    }

    data class Error(val message: String) : CamApsFxState
}