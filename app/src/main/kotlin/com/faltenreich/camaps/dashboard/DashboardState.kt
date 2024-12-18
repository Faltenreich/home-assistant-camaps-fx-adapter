package com.faltenreich.camaps.dashboard

import com.faltenreich.camaps.MainServiceState
import com.faltenreich.camaps.dashboard.log.LogEntry

sealed interface DashboardState {

    data object MissingPermissions : DashboardState

    data class Content(
        val service: MainServiceState,
        val log: List<LogEntry>,
    ) : DashboardState
}