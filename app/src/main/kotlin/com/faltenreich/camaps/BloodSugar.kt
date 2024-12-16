package com.faltenreich.camaps

data class BloodSugar(
    val mgDl: Float,
    val trend: Trend?,
) {

    enum class Trend(
        val camApsImageResourceId: Int,
    ) {
        RISING_FAST(
            camApsImageResourceId = -1, // TODO
        ),
        RISING(
            camApsImageResourceId = -1, // TODO
        ),
        RISING_SLOW(
            camApsImageResourceId = -1, // TODO
        ),
        STEADY(
            camApsImageResourceId = 2131230951,
        ),
        DROPPING_SLOW(
            camApsImageResourceId = 2131230942,
        ),
        DROPPING(
            camApsImageResourceId = -1, // TODO
        ),
        DROPPING_FAST(
            camApsImageResourceId = -1, // TODO
        ),
    }
}