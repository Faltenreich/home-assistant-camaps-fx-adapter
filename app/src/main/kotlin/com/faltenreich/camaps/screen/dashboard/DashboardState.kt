package com.faltenreich.camaps.screen.dashboard

import com.faltenreich.camaps.screen.dashboard.log.LogEntry

sealed interface DashboardState {

    data object Loading : DashboardState

    data object MissingPermission : DashboardState

    data class Content(
        val log: List<LogEntry>,
    ) : DashboardState
}