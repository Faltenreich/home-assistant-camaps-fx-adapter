package com.faltenreich.camaps.camaps

import android.service.notification.StatusBarNotification
import com.faltenreich.camaps.MainStateProvider
import com.faltenreich.camaps.camaps.notification.CamApsFxNotificationMapper

class CamApsFxController {

    private val mainStateProvider = MainStateProvider
    private val mapNotification = CamApsFxNotificationMapper()

    fun handleNotification(statusBarNotification: StatusBarNotification?): CamApsFxState? {
        val state = statusBarNotification?.let { mapNotification(statusBarNotification) } ?: return null
        mainStateProvider.setCamApsFxState(state)
        return state
    }
}
