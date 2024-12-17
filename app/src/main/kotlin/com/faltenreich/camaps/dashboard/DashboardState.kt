package com.faltenreich.camaps.dashboard

import com.faltenreich.camaps.MainState

sealed interface DashboardState {

    data object MissingNotificationListenerPermission : DashboardState

    data class Content(val mainState: MainState) : DashboardState
}