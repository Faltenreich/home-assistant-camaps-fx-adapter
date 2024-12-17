package com.faltenreich.camaps.camaps

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.faltenreich.camaps.MainStateHolder

class CamApsFxController : NotificationListenerService() {

    private val notificationMapper = CamApsFxNotificationMapper()
    private val mainStateHolder = MainStateHolder

    fun handleNotification(statusBarNotification: StatusBarNotification?) {
        val state = statusBarNotification?.let(notificationMapper::invoke) ?: return
        mainStateHolder.setCamApsFxState(state)
    }
}