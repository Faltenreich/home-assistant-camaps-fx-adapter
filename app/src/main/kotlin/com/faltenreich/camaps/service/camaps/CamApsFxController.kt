package com.faltenreich.camaps.service.camaps

import android.service.notification.StatusBarNotification
import com.faltenreich.camaps.AppStateProvider
import com.faltenreich.camaps.service.camaps.notification.CamApsFxNotificationMapper

class CamApsFxController (
    private val appStateProvider: AppStateProvider,
    private val mapNotification: CamApsFxNotificationMapper,
){

    fun handleNotification(statusBarNotification: StatusBarNotification?): CamApsFxState? {
        val state = statusBarNotification?.let { mapNotification(statusBarNotification) } ?: return null
        appStateProvider.setCamApsFxState(state)
        return state
    }
}
