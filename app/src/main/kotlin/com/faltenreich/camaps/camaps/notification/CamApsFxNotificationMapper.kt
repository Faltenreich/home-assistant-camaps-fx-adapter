@file:Suppress("DEPRECATION")

package com.faltenreich.camaps.camaps.notification

import android.service.notification.StatusBarNotification
import android.widget.RemoteViews
import com.faltenreich.camaps.camaps.CamApsFxState
import java.util.ArrayList
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class CamApsFxNotificationMapper {

    operator fun invoke(statusBarNotification: StatusBarNotification): CamApsFxState? {
        val camApsFxNotification = statusBarNotification
            .takeIf { it.packageName == CAM_APS_FX_PACKAGE_NAME }
            ?.notification
            ?: return null
        val contentView = camApsFxNotification.contentView ?: run {
            return CamApsFxState.Error("Missing contentView")
        }
        val actions = contentView.actions.takeIf(List<*>::isNotEmpty) ?: run {
            return CamApsFxState.Error("Missing actions")
        }
        val setTextActions = actions.filter { it.methodName == "setText" }

        val mgDl = setTextActions.mapNotNull { (it.value as? String)?.toFloatOrNull() }.firstOrNull()
        val isOff = setTextActions.any { (it.value as? String) == "Aus" }
        val isStarting = setTextActions.any { (it.value as? String) == "Starten" }

        return when {
            mgDl != null -> {
                val trendImageResourceId = actions
                    .filter { it.methodName == "setImageResource" }
                    .mapNotNull { it.value as? Int }
                    .lastOrNull()
                val trend = CamApsFxState.BloodSugar.Trend.entries
                    .firstOrNull { it.imageResourceId == trendImageResourceId }
                CamApsFxState.BloodSugar(mgDl, trend)
            }
            isStarting -> CamApsFxState.Starting
            isOff -> CamApsFxState.Off
            else -> {
                val actionsJoined = actions
                    .map { action -> "${action.methodName}: ${action.value}" }
                    .joinToString()
                CamApsFxState.Error(
                    message = "Unknown actions: $actionsJoined",
                )
            }
        }
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
                    .firstOrNull { it.name == "mMethodName" }
                    ?: return@mapNotNull null
                methodNameProperty.isAccessible = true
                val methodName = methodNameProperty.getter.call(action) as? String
                    ?: return@mapNotNull null

                val valueProperty = memberProperties
                    .firstOrNull { it.name == "mValue" }
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