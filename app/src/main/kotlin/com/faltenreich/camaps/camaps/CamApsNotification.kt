package com.faltenreich.camaps.camaps

data class CamApsNotification(
    val mgDl: Float,
    val trend: Trend = Trend.STEADY,
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