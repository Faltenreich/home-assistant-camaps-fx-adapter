package com.faltenreich.camaps.screen.dashboard

import com.faltenreich.camaps.screen.dashboard.log.LogEntry
import com.faltenreich.camaps.service.MainServiceState

sealed interface DashboardState {

    data object Loading : DashboardState

    data class Content(
        val serviceState: MainServiceState,
        val log: List<LogEntry>,
    ) : DashboardState
}