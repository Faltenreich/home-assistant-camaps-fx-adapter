@file:Suppress("DEPRECATION")

package com.faltenreich.camaps.camaps.notification

import android.content.Context
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

            val imageViews = mutableListOf<Int>()
            findImageViews(view, imageViews)
            val trendImageResourceIds =
                CamApsFxState.BloodSugar.Trend.entries.map { it.imageResourceId }
            val unknownImageResourceIds =
                imageViews.filter { it !in CamApsFxState.BloodSugar.LOGO_IMAGE_RESOURCE_IDS && it !in trendImageResourceIds }
            if (unknownImageResourceIds.isNotEmpty()) {
                Log.d(
                    TAG,
                    "Found unknown image resource IDs in notification: ${unknownImageResourceIds.joinToString()}"
                )
            }
            val trend = CamApsFxState.BloodSugar.Trend.entries
                .firstOrNull { trend -> imageViews.any { it == trend.imageResourceId } }
                ?: CamApsFxState.BloodSugar.Trend.UNKNOWN

            val value = textViews.mapNotNull { it.replace(',', '.').toFloatOrNull() }.firstOrNull()
            val unit = textViews.firstOrNull { it.equals("mmol/L", ignoreCase = true) || it.equals("mg/dL", ignoreCase = true) }

            return when {
                value != null && unit != null -> {
                    Log.d(TAG, "Current reading: $value $unit")
                    try {
                        CamApsFxState.BloodSugar(value, unit, trend)
                    } catch (e: Exception) {
                        CamApsFxState.Error("Failed to send reading: $value $unit")
                    }
                }
                else -> {
                    CamApsFxState.Error(
                        message = "Could not determine state from notification: ${textViews.joinToString()}",
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

    private fun findImageViews(view: View, imageViews: MutableList<Int>) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                findImageViews(view.getChildAt(i), imageViews)
            }
        } else if (view is ImageView) {
            // This is a bit of a hack, but it's the most reliable way to get the resource ID
            val resourceId = try {
                val field = view::class.java.getDeclaredField("mResource")
                field.isAccessible = true
                field.getInt(view)
            } catch (e: Exception) {
                -1
            }
            if (resourceId != -1) {
                imageViews.add(resourceId)
            }
        }
    }

    companion object {

        private val TAG = CamApsFxNotificationMapper::class.java.simpleName
        private const val CAM_APS_FX_PACKAGE_NAME_PREFIX = "com.camdiab.fx_alert"
    }
}
