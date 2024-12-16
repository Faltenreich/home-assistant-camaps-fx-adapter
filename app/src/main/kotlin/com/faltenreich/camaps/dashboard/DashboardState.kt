package com.faltenreich.camaps.dashboard

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantState

data class DashboardState(
    val camApsFxState: CamApsFxState,
    val homeAssistantState: HomeAssistantState,
)