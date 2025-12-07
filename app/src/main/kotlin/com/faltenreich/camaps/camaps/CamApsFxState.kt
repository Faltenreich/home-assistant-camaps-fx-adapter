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

        /**
         * This enum represents the possible trend directions.
         * The mapping to specific arrow images is now handled entirely by the
         * file-based system in the 'arrows' directory.
         */
        enum class Trend {
            RISING_FAST,
            RISING,
            RISING_SLOW,
            STEADY,
            DROPPING_SLOW,
            DROPPING,
            DROPPING_FAST,
            UNKNOWN,
            IGNORE;
        }
    }

    data class Error(val message: String) : CamApsFxState
}
