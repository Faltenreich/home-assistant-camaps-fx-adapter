package com.faltenreich.camaps

data class BloodSugarEvent(
    val mgDl: Float,
    val trend: Trend,
) {

    enum class Trend {
        RISING_FAST,
        RISING,
        RISING_SLOW,
        STEADY,
        DROPPING_SLOW,
        DROPPING,
        DROPPING_FAST,
    }
}