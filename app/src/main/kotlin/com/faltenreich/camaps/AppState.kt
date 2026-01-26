package com.faltenreich.camaps

import com.faltenreich.camaps.screen.dashboard.log.LogEntry

data class AppState(
    val log: List<LogEntry>,
)