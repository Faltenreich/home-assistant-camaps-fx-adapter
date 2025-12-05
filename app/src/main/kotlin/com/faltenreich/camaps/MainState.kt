package com.faltenreich.camaps

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.dashboard.log.LogEntry
import com.faltenreich.camaps.homeassistant.HomeAssistantState

data class MainState(
    val serviceState: MainServiceState = MainServiceState.Disconnected,
    val camApsFxState: CamApsFxState = CamApsFxState.Blank,
    val homeAssistantState: HomeAssistantState = HomeAssistantState.Idle,
    val log: List<LogEntry> = emptyList(),
)