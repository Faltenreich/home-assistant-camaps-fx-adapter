package com.faltenreich.camaps.camaps

import android.content.Context
import android.service.notification.StatusBarNotification
import com.faltenreich.camaps.MainStateProvider
import com.faltenreich.camaps.camaps.notification.CamApsFxNotificationMapper
import com.faltenreich.camaps.camaps.notification.TrendIconMapper

class CamApsFxController {

    private val mainStateProvider = MainStateProvider
    private val mapNotification = CamApsFxNotificationMapper(mapTrendIcon = TrendIconMapper())

    fun handleNotification(context: Context, statusBarNotification: StatusBarNotification?): CamApsFxState? {
        val state = statusBarNotification?.let { mapNotification(context, statusBarNotification) } ?: return null
        mainStateProvider.setCamApsFxState(state)
        return state
    }
}
