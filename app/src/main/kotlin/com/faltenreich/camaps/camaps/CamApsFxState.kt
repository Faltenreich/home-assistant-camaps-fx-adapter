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
        companion object {
            /**
             * These are the various logos for the CamAPS FX app itself, not trend arrows.
             * Place your logo image files (e.g., 'logo_green.png') in the `res/drawable/` folder,
             * then uncomment and add their `R.drawable.*` IDs to this list.
             */
            @DrawableRes
            val LOGO_IMAGE_RESOURCE_IDS = listOf(
                 R.drawable.logo_auto_mode_on,
                // R.drawable.logo_auto_mode_off,
                // R.drawable.logo_auto_mode_starting,
            )
        }

        /**
         * This enum maps a trend to the list of official image assets in res/drawable.
         * Place your arrow image files (e.g., 'arrow_stable_yellow.png') in `res/drawable/`
         * and add their `R.drawable.*` IDs here.
         */
        enum class Trend(@DrawableRes val imageResourceIds: List<Int>) {
            RISING_FAST(listOf(
                // R.drawable.arrow_rising_fast
            )),
            RISING(listOf(
                // R.drawable.arrow_rising
            )),
            RISING_SLOW(listOf(
                // R.drawable.arrow_rising_slow_gray,
                // R.drawable.arrow_rising_slow_yellow
            )),
            STEADY(listOf(
                 R.drawable.arrow_steady_gray,
                 R.drawable.arrow_steady_yellow,
                // R.drawable.arrow_stable_yellow // Your new arrow
            )),
            DROPPING_SLOW(listOf(
                // R.drawable.arrow_dropping_slow
            )),
            DROPPING(listOf(
                // R.drawable.arrow_dropping
            )),
            DROPPING_FAST(listOf(
                // R.drawable.arrow_dropping_fast
            )),
            UNKNOWN(listOf());
        }
    }

    data class Error(val message: String) : CamApsFxState
}
