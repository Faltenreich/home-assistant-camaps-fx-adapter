package com.faltenreich.camaps.dashboard.log

import java.time.LocalDateTime

data class LogEntry(
    val dateTime: LocalDateTime,
    val message: String,
)