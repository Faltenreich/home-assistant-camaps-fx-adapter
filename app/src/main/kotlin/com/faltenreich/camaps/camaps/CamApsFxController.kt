package com.faltenreich.camaps.camaps

import android.content.Context
import android.service.notification.StatusBarNotification
import com.faltenreich.camaps.MainStateProvider
import com.faltenreich.camaps.camaps.notification.CamApsFxNotificationMapper

class CamApsFxController {

    private val mainStateProvider = MainStateProvider
    private val notificationMapper = CamApsFxNotificationMapper()

    fun handleNotification(context: Context, statusBarNotification: StatusBarNotification?) {
        val state = statusBarNotification?.let { notificationMapper.invoke(context, it) } ?: return
        mainStateProvider.setCamApsFxState(state)
    }
}