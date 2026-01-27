package com.faltenreich.camaps.core.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object Colors {

    val Primary = Color(0xff00a89e)
    val Green = Color(0xff478063)

    val DarkColorScheme = darkColorScheme(
        primary = Primary,
        onPrimary = Color.White,
        primaryContainer = Primary,
        onPrimaryContainer = Color.White,
    )
    val LightColorScheme = lightColorScheme(
        primary = Primary,
        onPrimary = Color.White,
        primaryContainer = Primary,
        onPrimaryContainer = Color.White,
    )
}