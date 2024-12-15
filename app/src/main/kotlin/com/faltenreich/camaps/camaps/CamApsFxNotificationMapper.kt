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
        val notification = statusBarNotification
            .takeIf { it.packageName == CAM_APS_FX_PACKAGE_NAME }
            ?.notification
            ?: return null
        val contentView = notification.contentView ?: return null
        val actions = contentView.actions

        val mgDl = actions
            .filter { it.methodName == "setText" }
            .mapNotNull { (it.value as? String)?.toFloatOrNull() }
            .first()

        return BloodSugarEvent(
            mgDl = mgDl,
            trend = BloodSugarEvent.Trend.STEADY, // TODO
        )
    }

    private val RemoteViews.actions: List<RemoteViewAction>
        get() {
            val actionsProperty = RemoteViews::class.memberProperties.first { it.name == "mActions" }
            actionsProperty.isAccessible = true
            val actions = actionsProperty.get(this) as ArrayList<*>

            return actions.map { action ->
                val memberProperties = action::class.memberProperties

                val methodNameProperty = memberProperties.first { it.name == "methodName" }
                methodNameProperty.isAccessible = true
                val methodName = methodNameProperty.getter.call(action) as String

                val valueProperty = memberProperties.first { it.name == "value" }
                valueProperty.isAccessible = true
                val value = valueProperty.getter.call(action)

                RemoteViewAction(methodName, value)
            }
        }

    companion object {

        private const val CAM_APS_FX_PACKAGE_NAME = "com.camdiab.fx_alert.mgdl"
    }
}