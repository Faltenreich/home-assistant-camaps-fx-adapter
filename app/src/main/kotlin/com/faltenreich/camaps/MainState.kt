package com.faltenreich.camaps

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.dashboard.log.LogEntry
import com.faltenreich.camaps.homeassistant.HomeAssistantState

data class MainState(
    val serviceState: MainServiceState,
    val camApsFxState: CamApsFxState,
    val homeAssistantState: HomeAssistantState,
    val log: List<LogEntry>,
)