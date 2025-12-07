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
        companion object {
            val LOGO_IMAGE_RESOURCE_IDS = listOf(
                2131230905, /* Auto mode gray/off */
                2131230904, /* Auto mode orange/starting/stopping/attempting */
                2131230907, /* Auto mode green/on */
            )
        }

        enum class Trend(val imageResourceIds: List<Int>) {
            RISING_FAST(listOf(
                2131230956, /* Yellow? */
            )),
            RISING(listOf(2131230960)),
            RISING_SLOW(listOf(
                2131230954, /* Gray */
                2131230953, /* Yellow */
            )),
            STEADY(listOf(
                2131230951, /* Gray */
                2131230950, /* Yellow */
            )),
            DROPPING_SLOW(listOf(2131230942)),
            DROPPING(listOf(2131230949)),
            DROPPING_FAST(listOf(2131230946)),
            UNKNOWN(listOf(-1)),
        }
    }

    data class Error(val message: String) : CamApsFxState
}
