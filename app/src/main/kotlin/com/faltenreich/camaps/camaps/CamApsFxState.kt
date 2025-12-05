package com.faltenreich.camaps.camaps

sealed interface CamApsFxState {

    data object Blank : CamApsFxState

    data object Off : CamApsFxState

    data object Starting : CamApsFxState

    data class BloodSugar(
        val value: Float,
        val unitOfMeasurement: String,
        val trend: Trend?,
    ) : CamApsFxState {
        enum class Trend(val imageResourceId: Int) {
            RISING_FAST(-1), // TODO
            RISING(2131230960),
            RISING_SLOW(2131230954),
            STEADY(2131230951),
            DROPPING_SLOW(2131230942),
            DROPPING(2131230949),
            DROPPING_FAST(2131230946),
        }
    }

    data class Error(val message: String) : CamApsFxState
}