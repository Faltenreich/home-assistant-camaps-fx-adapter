package com.faltenreich.camaps.camaps

import androidx.annotation.DrawableRes
import com.faltenreich.camaps.R

sealed interface CamApsFxState {

    data object Blank : CamApsFxState
    data object Off : CamApsFxState
    data object Starting : CamApsFxState

    data class BloodSugar(
        val value: Float,
        val unitOfMeasurement: String,
        val trend: Trend?,
    ) : CamApsFxState {
        enum class Trend(@DrawableRes val imageResourceIds: List<Int>) {
            RISING_FAST(listOf(
                R.drawable.rising_fast_yellow,
                R.drawable.rising_fast_red,
                R.drawable.rising_fast_gray,
            )),
            RISING(listOf(
                R.drawable.rising_yellow,
                R.drawable.rising_red,
                R.drawable.rising_gray,
            )),
            RISING_SLOW(listOf(
                R.drawable.rising_slow_yellow,
                R.drawable.rising_slow_red,
                R.drawable.rising_slow_gray,
            )),
            STEADY(listOf(
                R.drawable.steady_yellow,
                R.drawable.steady_red,
                R.drawable.steady_gray,
            )),
            DROPPING_SLOW(listOf(
                R.drawable.dropping_slow_yellow,
                R.drawable.dropping_slow_red,
                R.drawable.dropping_slow_gray,
            )),
            DROPPING(listOf(
                R.drawable.dropping_yellow,
                R.drawable.dropping_red,
                R.drawable.dropping_gray,
            )),
            DROPPING_FAST(listOf(
                R.drawable.dropping_fast_yellow,
                R.drawable.dropping_fast_red,
                R.drawable.dropping_fast_gray,
            )),
            UNKNOWN(listOf());
        }
    }

    data class Error(val message: String) : CamApsFxState
}

fun CamApsFxState.BloodSugar.Trend?.toArrow(): String = when (this) {
    CamApsFxState.BloodSugar.Trend.RISING_FAST -> "↟"
    CamApsFxState.BloodSugar.Trend.RISING -> "↑"
    CamApsFxState.BloodSugar.Trend.RISING_SLOW -> "↗"
    CamApsFxState.BloodSugar.Trend.STEADY -> "→"
    CamApsFxState.BloodSugar.Trend.DROPPING_SLOW -> "↘"
    CamApsFxState.BloodSugar.Trend.DROPPING -> "↓"
    CamApsFxState.BloodSugar.Trend.DROPPING_FAST -> "↡"
    else -> ""
}
