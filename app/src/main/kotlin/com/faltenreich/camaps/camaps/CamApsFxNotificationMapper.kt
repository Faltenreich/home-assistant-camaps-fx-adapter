package com.faltenreich.camaps.camaps

import android.service.notification.StatusBarNotification
import android.widget.RemoteViews
import com.faltenreich.camaps.BloodSugar
import java.util.ArrayList
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class CamApsFxNotificationMapper {

    operator fun invoke(statusBarNotification: StatusBarNotification): CamApsFxState? {
        val camApsFxNotification = statusBarNotification
            .takeIf { it.packageName == CAM_APS_FX_PACKAGE_NAME }
            ?.notification
            ?: return null
        @Suppress("DEPRECATION")
        val contentView = camApsFxNotification.contentView ?: return null
        val actions = contentView.actions.takeIf(List<*>::isNotEmpty) ?: return null

        val mgDl = actions
            .filter { it.methodName == "setText" }
            .mapNotNull { (it.value as? String)?.toFloatOrNull() }
            .firstOrNull()
            ?: return null

        val trendImageResourceId = actions
            .filter { it.methodName == "setImageResource" }
            .mapNotNull { it.value as? Int }
            .lastOrNull()
        val trend = BloodSugar.Trend.entries.firstOrNull {
            it.camApsImageResourceId == trendImageResourceId
        }

        return CamApsFxState.Value(
            bloodSugar = BloodSugar(
                mgDl = mgDl,
                trend = trend,
            ),
        )
    }

    private val RemoteViews.actions: List<RemoteViewAction>
        get() {
            val actionsProperty = RemoteViews::class.memberProperties
                .firstOrNull { it.name == "mActions" }
                ?: return emptyList()
            actionsProperty.isAccessible = true
            val actions = actionsProperty.get(this) as? ArrayList<*> ?: return emptyList()

            return actions.mapNotNull { action ->
                val memberProperties = action::class.memberProperties

                val methodNameProperty = memberProperties
                    .firstOrNull { it.name == "methodName" }
                    ?: return@mapNotNull null
                methodNameProperty.isAccessible = true
                val methodName = methodNameProperty.getter.call(action) as? String
                    ?: return@mapNotNull null

                val valueProperty = memberProperties
                    .firstOrNull { it.name == "value" }
                    ?: return@mapNotNull null
                valueProperty.isAccessible = true
                val value = valueProperty.getter.call(action)

                RemoteViewAction(methodName, value)
            }
        }

    companion object {

        private const val CAM_APS_FX_PACKAGE_NAME = "com.camdiab.fx_alert.mgdl"
    }
}