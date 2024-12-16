package com.faltenreich.camaps

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class BloodSugar(
    val mgDl: Float,
    val trend: Trend?,
) {

    enum class Trend(
        val camApsImageResourceId: Int,
        val imageVector: ImageVector,
        val color: Color,
    ) {
        RISING_FAST(
            camApsImageResourceId = -1, // TODO
            imageVector = Icons.Default.KeyboardArrowUp,
            color = Color.Red,
        ),
        RISING(
            camApsImageResourceId = -1, // TODO
            imageVector = Icons.Default.KeyboardArrowUp,
            color = Color.Yellow,
        ),
        RISING_SLOW(
            camApsImageResourceId = -1, // TODO
            imageVector = Icons.Default.KeyboardArrowUp,
            color = Color.Gray,
        ),
        STEADY(
            camApsImageResourceId = 2131230951,
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            color = Color.Gray,
        ),
        DROPPING_SLOW(
            camApsImageResourceId = 2131230942,
            imageVector = Icons.Default.KeyboardArrowDown,
            color = Color.Gray,
        ),
        DROPPING(
            camApsImageResourceId = -1, // TODO
            imageVector = Icons.Default.KeyboardArrowDown,
            color = Color.Yellow,
        ),
        DROPPING_FAST(
            camApsImageResourceId = -1, // TODO
            imageVector = Icons.Default.KeyboardArrowDown,
            color = Color.Red,
        ),
    }
}