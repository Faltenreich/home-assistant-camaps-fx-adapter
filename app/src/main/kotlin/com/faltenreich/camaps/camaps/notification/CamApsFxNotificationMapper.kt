@file:Suppress("DEPRECATION")

package com.faltenreich.camaps.camaps.notification

import android.content.Context
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.faltenreich.camaps.camaps.CamApsFxState

class CamApsFxNotificationMapper {

    operator fun invoke(context: Context, statusBarNotification: StatusBarNotification): CamApsFxState? {
        val camApsFxNotification = statusBarNotification
            .takeIf { it.packageName.startsWith(CAM_APS_FX_PACKAGE_NAME_PREFIX) }
            ?.notification
            ?: return null

        // Prioritize bigContentView as it often contains more details than the collapsed view
        val remoteViews = camApsFxNotification.bigContentView ?: camApsFxNotification.contentView ?: run {
            return CamApsFxState.Error("Missing both bigContentView and contentView")
        }

        try {
            val remotePackageContext = context.createPackageContext(statusBarNotification.packageName, Context.CONTEXT_IGNORE_SECURITY)
            val view = remoteViews.apply(remotePackageContext, null)
            val textViews = mutableListOf<String>()
            findTextViews(view, textViews)
            Log.d(TAG, "Found text in notification: ${textViews.joinToString()}")

            val mmolL = textViews.mapNotNull { it.replace(',', '.').toFloatOrNull() }.firstOrNull()
            val isOff = textViews.any { it.equals("Aus", ignoreCase = true) }
            val isStarting = textViews.any { it.equals("Starten", ignoreCase = true) }

            return when {
                mmolL != null -> {
                    // Trend information might require visual analysis or is located in a different view.
                    // For now, we are focusing on the blood sugar value.
                    CamApsFxState.BloodSugar(mmolL, null)
                }
                isStarting -> CamApsFxState.Starting
                isOff -> CamApsFxState.Off
                else -> {
                    CamApsFxState.Error(
                        message = "Unknown text in notification: ${textViews.joinToString()}",
                    )
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error inflating remote views", e)
            return CamApsFxState.Error("Failed to inflate RemoteViews: ${e.message}")
        }
    }

    private fun findTextViews(view: View, textViews: MutableList<String>) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                findTextViews(view.getChildAt(i), textViews)
            }
        } else if (view is TextView) {
            val text = view.text?.toString()
            if (!text.isNullOrBlank()) {
                textViews.add(text)
            }
        }
    }

    companion object {

        private val TAG = CamApsFxNotificationMapper::class.java.simpleName
        private const val CAM_APS_FX_PACKAGE_NAME_PREFIX = "com.camdiab.fx_alert"
    }
}
