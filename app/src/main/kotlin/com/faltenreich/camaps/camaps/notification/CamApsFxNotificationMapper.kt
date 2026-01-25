package com.faltenreich.camaps.camaps.notification

import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.service.notification.StatusBarNotification
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import com.faltenreich.camaps.camaps.CamApsFxState

class CamApsFxNotificationMapper(private val mapTrendIcon: TrendIconMapper) {

    operator fun invoke(context: Context, statusBarNotification: StatusBarNotification): CamApsFxState? {
        val camApsFxNotification = statusBarNotification
            .takeIf { it.packageName.startsWith(CAM_APS_FX_PACKAGE_NAME_PREFIX) }
            ?.notification
            ?: return null
        val remoteViews = camApsFxNotification.remoteViews ?: run {
            return CamApsFxState.Error("Missing RemoteViews")
        }

        try {
            val remotePackageContext = context.createPackageContext(statusBarNotification.packageName, Context.CONTEXT_RESTRICTED)
            val view = remoteViews.apply(remotePackageContext, null)

            val textViews = mutableListOf<String>()
            findTexts(view, textViews)

            val extractedBitmaps = mutableListOf<Bitmap>()
            findBitmaps(view, extractedBitmaps)

            val trendBitmap = extractedBitmaps.getOrNull(1)
            val trend = trendBitmap?.let { mapTrendIcon(context, trendBitmap) } ?: CamApsFxState.BloodSugar.Trend.UNKNOWN

            val value = textViews.firstNotNullOfOrNull { it.replace(',', '.').toFloatOrNull() }
            val unitOfMeasurement = textViews.firstOrNull { it.equals("mmol/L", ignoreCase = true) || it.equals("mg/dL", ignoreCase = true) }

            return when {
                value != null && unitOfMeasurement != null -> CamApsFxState.BloodSugar(value, unitOfMeasurement, trend)
                else -> CamApsFxState.Error("Could not determine state from notification: ${textViews.joinToString()}")
            }
        } catch (exception: Exception) {
            return CamApsFxState.Error("Failed to process notification: ${exception.message}")
        }
    }

    @Suppress("Deprecation")
    private val Notification.remoteViews: RemoteViews?
        get() = bigContentView ?: contentView

    private fun findTexts(view: View, out: MutableList<String>) {
        when (view) {
            is TextView -> view.text?.toString()?.takeIf(String::isNotBlank)?.let(out::add)
            is ViewGroup -> view.children.forEach { findTexts(it, out) }
        }
    }

    private fun findBitmaps(view: View, out: MutableList<Bitmap>) {
        when (view) {
            is ImageView if view.drawable != null -> out.add(view.drawable.toBitmap())
            is ViewGroup -> view.children.forEach { findBitmaps(it, out) }
        }
    }

    companion object {

        private const val CAM_APS_FX_PACKAGE_NAME_PREFIX = "com.camdiab.fx_alert"
    }
}
