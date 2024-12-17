package com.faltenreich.camaps.dashboard.log

import androidx.compose.ui.graphics.Color

data class LogEntry(
    val dateTime: String,
    val source: Source,
    val message: String,
) {

    data class Source(
        val name: String,
        val color: Color,
    )
}