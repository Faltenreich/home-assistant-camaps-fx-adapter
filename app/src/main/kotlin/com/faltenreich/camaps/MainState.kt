package com.faltenreich.camaps

import com.faltenreich.camaps.screen.dashboard.log.LogEntry
import com.faltenreich.camaps.service.camaps.CamApsFxState

data class MainState(
    val permission: Permission,
    val camApsFxState: CamApsFxState,
    val log: List<LogEntry>,
) {

    sealed interface Permission {

        data object Loading : Permission

        data object Granted : Permission

        data object Denied : Permission
    }
}