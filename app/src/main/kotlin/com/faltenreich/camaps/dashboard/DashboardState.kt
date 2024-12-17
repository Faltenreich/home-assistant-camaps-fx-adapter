package com.faltenreich.camaps.dashboard

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.dashboard.log.LogEntry
import com.faltenreich.camaps.homeassistant.HomeAssistantState

data class DashboardState(
    val camApsFx: CamApsFxState,
    val homeAssistant: HomeAssistantState,
    val log: List<LogEntry>,
)