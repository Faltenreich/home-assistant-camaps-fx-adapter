package com.faltenreich.camaps.camaps

sealed interface CamApsFxState {

    data object Blank : CamApsFxState

    data object Off : CamApsFxState

    data object Starting : CamApsFxState

    data class BloodSugar(
        val mmolL: Float,
        val trend: Trend?,
    ) : CamApsFxState {

        enum class Trend(val imageResourceId: Int) {
            RISING_FAST(-1), // TODO
            RISING(2131230960),
            RISING_SLOW(2131230954),
            STEADY(2131230951),
            DROPPING_SLOW(2131230942),
            DROPPING(-1), // TODO
            DROPPING_FAST(-1), // TODO
        }
    }

    data class Error(val message: String) : CamApsFxState
}