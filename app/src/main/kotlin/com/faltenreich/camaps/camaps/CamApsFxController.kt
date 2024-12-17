package com.faltenreich.camaps.camaps

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.faltenreich.camaps.MainStateProvider
import com.faltenreich.camaps.camaps.notification.CamApsFxNotificationMapper

class CamApsFxController : NotificationListenerService() {

    private val mainStateProvider = MainStateProvider
    private val notificationMapper = CamApsFxNotificationMapper()

    fun handleNotification(statusBarNotification: StatusBarNotification?) {
        val state = statusBarNotification?.let(notificationMapper::invoke) ?: return
        mainStateProvider.setCamApsFxState(state)
    }
}