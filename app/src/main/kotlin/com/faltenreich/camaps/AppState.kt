package com.faltenreich.camaps

import com.faltenreich.camaps.screen.dashboard.log.LogEntry
import com.faltenreich.camaps.service.camaps.CamApsFxState

data class AppState(
    val camApsFxState: CamApsFxState,
    val log: List<LogEntry>,
)