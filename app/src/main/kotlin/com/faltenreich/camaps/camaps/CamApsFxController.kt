package com.faltenreich.camaps.camaps

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.faltenreich.camaps.MainStateProvider

class CamApsFxController : NotificationListenerService() {

    private val notificationMapper = CamApsFxNotificationMapper()
    private val mainStateProvider = MainStateProvider

    fun handleNotification(statusBarNotification: StatusBarNotification?) {
        val state = statusBarNotification?.let(notificationMapper::invoke) ?: return
        mainStateProvider.setCamApsFxState(state)
    }
}