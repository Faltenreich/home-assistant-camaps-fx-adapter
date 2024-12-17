package com.faltenreich.camaps.camaps

sealed interface CamApsFxState {

    data object Blank : CamApsFxState

    data class BloodSugar(
        val mgDl: Float,
        val trend: Trend?,
    ) : CamApsFxState {

        enum class Trend(val camApsImageResourceId: Int) {
            RISING_FAST(-1), // TODO
            RISING(-1), // TODO
            RISING_SLOW(2131230954),
            STEADY(2131230951),
            DROPPING_SLOW(2131230942),
            DROPPING(-1), // TODO
            DROPPING_FAST(-1), // TODO
        }
    }

    data class Unknown(val message: String) : CamApsFxState
}