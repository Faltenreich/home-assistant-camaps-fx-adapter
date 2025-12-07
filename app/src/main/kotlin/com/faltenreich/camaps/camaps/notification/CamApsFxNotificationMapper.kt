@file:Suppress("DEPRECATION")

package com.faltenreich.camaps.camaps.notification

import android.content.Context
import android.graphics.Bitmap
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import com.faltenreich.camaps.camaps.CamApsFxState

class CamApsFxNotificationMapper {

    operator fun invoke(context: Context, statusBarNotification: StatusBarNotification): CamApsFxState? {
        val camApsFxNotification = statusBarNotification
            .takeIf { it.packageName.startsWith(CAM_APS_FX_PACKAGE_NAME_PREFIX) }
            ?.notification
            ?: return null

        val remoteViews = camApsFxNotification.bigContentView ?: camApsFxNotification.contentView ?: run {
            return CamApsFxState.Error("Missing both bigContentView and contentView")
        }

        try {
            val remotePackageContext = context.createPackageContext(statusBarNotification.packageName, Context.CONTEXT_IGNORE_SECURITY)
            val view = remoteViews.apply(remotePackageContext, null)

            val textViews = mutableListOf<String>()
            findTextViews(view, textViews)

            val arrowBitmaps = mutableListOf<Bitmap>()
            findImageViewBitmaps(view, arrowBitmaps)

            var detectedTrend = CamApsFxState.BloodSugar.Trend.UNKNOWN

            for (bitmap in arrowBitmaps) {
                val matchedTrend = TrendMappingManager.matchTrend(bitmap, context)

                if (matchedTrend == CamApsFxState.BloodSugar.Trend.UNKNOWN) {
                    TrendMappingManager.saveNewBitmap(context, bitmap)
                } else if (matchedTrend != CamApsFxState.BloodSugar.Trend.IGNORE) {
                    detectedTrend = matchedTrend
                    Log.d(TAG, "Notification bitmap matched Trend: $matchedTrend")
                    break
                }
            }

            val value = textViews.mapNotNull { it.replace(',', '.').toFloatOrNull() }.firstOrNull()
            val unit = textViews.firstOrNull { it.equals("mmol/L", ignoreCase = true) || it.equals("mg/dL", ignoreCase = true) }

            return when {
                value != null && unit != null -> CamApsFxState.BloodSugar(value, unit, detectedTrend)
                else -> CamApsFxState.Error("Could not determine state from notification: ${textViews.joinToString()}")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error processing notification", e)
            return CamApsFxState.Error("Failed to process notification: ${e.message}")
        }
    }

    private fun findTextViews(view: View, out: MutableList<String>) {
        if (view is TextView) {
            view.text?.toString()?.takeIf { it.isNotBlank() }?.let(out::add)
        }
        if (view is ViewGroup) {
            (0 until view.childCount).forEach { findTextViews(view.getChildAt(it), out) }
        }
    }

    private fun findImageViewBitmaps(view: View, out: MutableList<Bitmap>) {
        if (view is ImageView && view.drawable != null) {
            out.add(view.drawable.toBitmap())
        }
        if (view is ViewGroup) {
            (0 until view.childCount).forEach { findImageViewBitmaps(view.getChildAt(it), out) }
        }
    }

    companion object {
        private val TAG = CamApsFxNotificationMapper::class.java.simpleName
        private const val CAM_APS_FX_PACKAGE_NAME_PREFIX = "com.camdiab.fx_alert"
    }
}
