package com.faltenreich.camaps.service.camaps

import android.service.notification.StatusBarNotification
import com.faltenreich.camaps.AppStateProvider
import com.faltenreich.camaps.service.camaps.notification.CamApsFxNotificationMapper

class CamApsFxController (
    private val appStateProvider: AppStateProvider,
    private val mapNotification: CamApsFxNotificationMapper,
){

    suspend fun handleNotification(statusBarNotification: StatusBarNotification?): CamApsFxEvent? {
        val state = statusBarNotification?.let { mapNotification(statusBarNotification) } ?: return null
        appStateProvider.postEvent(state)
        return state
    }
}
