@file:Suppress("DEPRECATION")

package com.faltenreich.camaps.camaps.notification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import com.faltenreich.camaps.camaps.CamApsFxState
import java.io.File
import java.io.FileOutputStream

class CamApsFxNotificationMapper {

    // Cache for our labeled arrow pHashes, loaded from res/drawable and the filesystem
    private val labeledHashes = mutableMapOf<CamApsFxState.BloodSugar.Trend, MutableList<Long>>()
    private val HAMMING_DISTANCE_THRESHOLD = 5

    operator fun invoke(context: Context, statusBarNotification: StatusBarNotification): CamApsFxState? {
        val camApsFxNotification = statusBarNotification
            .takeIf { it.packageName.startsWith(CAM_APS_FX_PACKAGE_NAME_PREFIX) }
            ?.notification
            ?: return null

        if (labeledHashes.isEmpty()) {
            loadAllLabeledBitmaps(context)
        }

        val remoteViews = camApsFxNotification.bigContentView ?: camApsFxNotification.contentView ?: run {
            return CamApsFxState.Error("Missing both bigContentView and contentView")
        }

        try {
            val remotePackageContext = context.createPackageContext(statusBarNotification.packageName, Context.CONTEXT_IGNORE_SECURITY)
            val view = remoteViews.apply(remotePackageContext, null)

            val textViews = mutableListOf<String>()
            findTextViews(view, textViews)

            val extractedBitmaps = mutableListOf<Bitmap>()
            findImageViewBitmaps(view, extractedBitmaps)

            val logoHashes = CamApsFxState.BloodSugar.LOGO_IMAGE_RESOURCE_IDS.mapNotNull {
                try { BitmapFactory.decodeResource(context.resources, it).pHash() } catch (e: Exception) { null }
            }
            val arrowBitmaps = extractedBitmaps.filterNot { extracted ->
                val extractedHash = extracted.pHash()
                logoHashes.any { logoHash -> extractedHash.hammingDistance(logoHash) <= HAMMING_DISTANCE_THRESHOLD }
            }

            var detectedTrend = CamApsFxState.BloodSugar.Trend.UNKNOWN

            for (bitmap in arrowBitmaps) {
                val matchedTrend = matchTrend(bitmap)
                if (matchedTrend != CamApsFxState.BloodSugar.Trend.UNKNOWN) {
                    detectedTrend = matchedTrend
                    Log.d(TAG, "Notification bitmap matched Trend: $matchedTrend")
                    break
                } else {
                    saveNewBitmap(context, bitmap)
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

    /**
     * Compares an extracted bitmap's pHash against all known, labeled pHashes.
     */
    private fun matchTrend(extractedBitmap: Bitmap): CamApsFxState.BloodSugar.Trend {
        val extractedHash = extractedBitmap.pHash()
        for ((trend, hashes) in labeledHashes) {
            if (hashes.any { labeledHash -> labeledHash.hammingDistance(extractedHash) <= HAMMING_DISTANCE_THRESHOLD }) {
                return trend
            }
        }
        return CamApsFxState.BloodSugar.Trend.UNKNOWN
    }

    /**
     * Loads all known arrow bitmaps, computes their pHashes, and caches them.
     */
    private fun loadAllLabeledBitmaps(context: Context) {
        Log.d(TAG, "Loading and hashing all labeled trend bitmaps...")
        CamApsFxState.BloodSugar.Trend.values().forEach { trend ->
            trend.imageResourceIds.forEach { resId ->
                try {
                    val bitmap = BitmapFactory.decodeResource(context.resources, resId)
                    labeledHashes.getOrPut(trend) { mutableListOf() }.add(bitmap.pHash())
                } catch (e: Exception) { /* Ignore placeholder IDs */ }
            }
        }

        val arrowsDir = context.getExternalFilesDir("arrows")
        arrowsDir?.listFiles { _, name -> name.endsWith(".png") }?.forEach { file ->
            try {
                val trendName = file.nameWithoutExtension.substringBefore("_").uppercase()
                val trend = CamApsFxState.BloodSugar.Trend.valueOf(trendName)
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val pHash = bitmap.pHash()
                if (labeledHashes.values.flatten().none { it == pHash }) {
                    labeledHashes.getOrPut(trend) { mutableListOf() }.add(pHash)
                    Log.d(TAG, "Loaded user-labeled trend from file: ${file.name}")
                }
            } catch (e: Exception) { /* Ignore improperly named files */ }
        }
    }

    /**
     * Saves a new, unrecognized bitmap to the "arrows" folder if its pHash is not close to any known hash.
     */
    private fun saveNewBitmap(context: Context, bitmap: Bitmap) {
        val arrowsDir = context.getExternalFilesDir("arrows") ?: return
        if (!arrowsDir.exists()) arrowsDir.mkdirs()

        val newHash = bitmap.pHash()
        val alreadyExists = labeledHashes.values.flatten().any { knownHash ->
            knownHash.hammingDistance(newHash) <= HAMMING_DISTANCE_THRESHOLD
        }

        if (!alreadyExists) {
            val fileName = "unknown_${System.currentTimeMillis()}.png"
            val file = File(arrowsDir, fileName)
            try {
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    Log.i(TAG, "SAVED NEW UNKNOWN ARROW to: ${file.absolutePath}. Please rename it to label it.")
                }
                labeledHashes.getOrPut(CamApsFxState.BloodSugar.Trend.UNKNOWN) { mutableListOf() }.add(newHash)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save new bitmap", e)
            }
        }
    }

    // --- pHash and Traversal Helpers ---

    /**
     * Computes a 64-bit "average hash" (aHash) for a bitmap.
     * This is a simple type of perceptual hash resistant to minor variations.
     */
    private fun Bitmap.pHash(): Long {
        // 1. Resize to a small, fixed size (e.g., 8x8).
        val smallBitmap = Bitmap.createScaledBitmap(this, 8, 8, true)
        var hash: Long = 0
        var averagePixelValue = 0

        // 2. Convert to grayscale and calculate the average pixel value.
        for (y in 0 until 8) {
            for (x in 0 until 8) {
                averagePixelValue += Color.red(smallBitmap.getPixel(x, y))
            }
        }
        averagePixelValue /= 64

        // 3. For each pixel, create a bit: 1 if brighter than average, 0 if darker.
        for (y in 0 until 8) {
            for (x in 0 until 8) {
                hash = (hash shl 1) or if (Color.red(smallBitmap.getPixel(x, y)) > averagePixelValue) 1 else 0
            }
        }
        return hash
    }

    /**
     * Calculates the Hamming distance between two 64-bit hashes.
     * This counts how many bits are different.
     */
    private fun Long.hammingDistance(other: Long): Int {
        return java.lang.Long.bitCount(this xor other)
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