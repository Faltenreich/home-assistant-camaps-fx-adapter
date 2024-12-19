package com.faltenreich.camaps.dashboard.log

data class LogEntry(
    val dateTime: String,
    val source: String,
    val message: String,
)