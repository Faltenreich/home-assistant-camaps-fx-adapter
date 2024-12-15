package com.faltenreich.camaps.camaps

import android.service.notification.StatusBarNotification
import android.widget.RemoteViews
import com.faltenreich.camaps.BloodSugarEvent
import java.util.ArrayList
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class CamApsFxNotificationMapper {

    @Suppress("DEPRECATION")
    operator fun invoke(statusBarNotification: StatusBarNotification): BloodSugarEvent? {
        // Notification
        val notification = statusBarNotification
            .takeIf { it.packageName == CAM_APS_FX_PACKAGE_NAME }
            ?.notification
            ?: return null
        val contentView = notification.contentView ?: return null

        // RemoteViews
        val actionsProperty = RemoteViews::class.memberProperties.first { it.name == "mActions" }
        actionsProperty.isAccessible = true
        val actions = actionsProperty.get(contentView) as ArrayList<*>

        // CamAPS FX
        val action = actions[2] // TODO: Get action for setText with numeric String als value
        val valueProperty = action::class.memberProperties.first { it.name == "value" }
        valueProperty.isAccessible = true
        val value = valueProperty.getter.call(action)
        val mgDl = (value as? String)?.toFloatOrNull() ?: return null

        return BloodSugarEvent(
            mgDl = mgDl,
            trend = BloodSugarEvent.Trend.STEADY, // TODO
        )
    }

    companion object {

        private const val CAM_APS_FX_PACKAGE_NAME = "com.camdiab.fx_alert.mgdl"
    }
}