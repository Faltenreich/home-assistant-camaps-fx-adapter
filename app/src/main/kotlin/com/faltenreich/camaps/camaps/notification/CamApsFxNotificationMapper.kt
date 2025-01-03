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
        val remoteViews = camApsFxNotification.contentView ?: run {
            return CamApsFxState.Error("Missing contentView")
        }
        val remoteViewActions = getRemoteViewActions(remoteViews).takeIf(List<*>::isNotEmpty) ?: run {
            return CamApsFxState.Error("Missing actions")
        }
        val setTextActions = remoteViewActions.filter { it.methodName == "setText" }

        val mgDl = setTextActions.mapNotNull { (it.value as? String)?.toFloatOrNull() }.firstOrNull()
        val isOff = setTextActions.any { (it.value as? String) == "Aus" }
        val isStarting = setTextActions.any { (it.value as? String) == "Starten" }

        return when {
            mgDl != null -> {
                val trendImageResourceId = remoteViewActions
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
                val actionsJoined = remoteViewActions
                    .map { action -> "${action.methodName}: ${action.value}" }
                    .joinToString()
                CamApsFxState.Error(
                    message = "Unknown actions: $actionsJoined",
                )
            }
        }
    }

    private fun getRemoteViewActions(from: RemoteViews): List<RemoteViewAction> {
        val actionsProperty = RemoteViews::class.memberProperties
            .firstOrNull { it.name == "mActions" }
            ?: return emptyList()
        actionsProperty.isAccessible = true
        val actions = actionsProperty.get(from) as? ArrayList<*> ?: return emptyList()
        return actions.mapNotNull(::getRemoteViewAction)
    }

    private fun getRemoteViewAction(from: Any): RemoteViewAction? {
        val memberProperties = from::class.memberProperties

        val methodNameProperty = memberProperties
            .firstOrNull { it.name == "mMethodName" }
            ?: return null
        methodNameProperty.isAccessible = true
        val methodName = methodNameProperty.getter.call(from) as? String ?: return null

        val valueProperty = memberProperties.firstOrNull { it.name == "mValue" } ?: return null
        valueProperty.isAccessible = true
        val value = valueProperty.getter.call(from)

        return RemoteViewAction(methodName, value)
    }

    companion object {

        private const val CAM_APS_FX_PACKAGE_NAME = "com.camdiab.fx_alert.mgdl"
    }
}