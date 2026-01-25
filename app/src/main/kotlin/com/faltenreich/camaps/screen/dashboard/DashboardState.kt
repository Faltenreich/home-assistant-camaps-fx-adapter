package com.faltenreich.camaps.screen.dashboard

import com.faltenreich.camaps.service.MainServiceState
import com.faltenreich.camaps.screen.dashboard.log.LogEntry

sealed interface DashboardState {

    data object MissingPermissions : DashboardState

    data class Content(
        val serviceState: MainServiceState,
        val log: List<LogEntry>,
    ) : DashboardState
}