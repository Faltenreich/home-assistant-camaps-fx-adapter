package com.faltenreich.camaps

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.dashboard.log.LogEntry
import com.faltenreich.camaps.homeassistant.HomeAssistantState

data class MainState(
    val camApsFx: CamApsFxState,
    val homeAssistant: HomeAssistantState,
    val log: List<LogEntry>,
)