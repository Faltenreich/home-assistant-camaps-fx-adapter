package com.faltenreich.camaps

import com.faltenreich.camaps.screen.dashboard.log.LogEntry
import com.faltenreich.camaps.service.MainServiceState
import com.faltenreich.camaps.service.camaps.CamApsFxState
import com.faltenreich.camaps.service.homeassistant.HomeAssistantState

data class MainState(
    val permission: Permission,
    val serviceState: MainServiceState,
    val camApsFxState: CamApsFxState,
    val homeAssistantState: HomeAssistantState,
    val log: List<LogEntry>,
) {

    sealed interface Permission {

        data object Loading : Permission

        data object Granted : Permission

        data object Denied : Permission
    }
}