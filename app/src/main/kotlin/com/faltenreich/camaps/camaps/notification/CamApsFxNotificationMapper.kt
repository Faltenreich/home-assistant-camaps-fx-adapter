package com.faltenreich.camaps.camaps.notification

import android.app.Notification
import android.service.notification.StatusBarNotification
import android.widget.RemoteViews
import com.faltenreich.camaps.camaps.CamApsFxState
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class CamApsFxNotificationMapper {

    @Suppress("Deprecation")
    private val Notification.remoteViews: RemoteViews?
        get() = contentView ?: bigContentView

    operator fun invoke(statusBarNotification: StatusBarNotification): CamApsFxState? {
        val camApsFxNotification = statusBarNotification
            .takeIf { it.packageName.startsWith(CAM_APS_FX_PACKAGE_NAME_PREFIX) }
            ?.notification
            ?: return null
        val remoteViews = camApsFxNotification.remoteViews ?: run {
            return CamApsFxState.Error("Missing contentView")
        }
        val remoteViewActions = getRemoteViewActions(remoteViews).takeIf(List<*>::isNotEmpty) ?: run {
            return CamApsFxState.Error("Missing actions")
        }
        val setTextActions = remoteViewActions.filter { it.methodName == "setText" }

        val value = setTextActions.firstNotNullOfOrNull { (it.value as? String)?.toFloatOrNull() }
        val unitOfMeasurement = setTextActions.mapNotNull {
            val text = it.value as? String ?: return@mapNotNull null
            val isUnit = text.equals(CAM_APS_FX_UNIT_MOLAR, ignoreCase = true) ||
                text.equals(CAM_APS_FX_UNIT_MASS, ignoreCase = true)
            if (isUnit) text else null
        }.firstOrNull()

        return when {
            value != null && unitOfMeasurement != null -> CamApsFxState.BloodSugar(value, unitOfMeasurement)
            else -> {
                val actionsJoined = remoteViewActions.joinToString { action ->
                    "${action.methodName}: ${action.value}"
                }
                CamApsFxState.Error(message = "Unknown actions: $actionsJoined")
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

        private const val CAM_APS_FX_PACKAGE_NAME_PREFIX = "com.camdiab.fx_alert"
        private const val CAM_APS_FX_UNIT_MOLAR = "mmol/L"
        private const val CAM_APS_FX_UNIT_MASS = "mg/dL"
    }
}
