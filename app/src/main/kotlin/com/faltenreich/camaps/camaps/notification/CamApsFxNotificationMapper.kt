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

    // Cache for our labeled arrow pHashes, loaded from the filesystem
    private val labeledHashes = mutableMapOf<CamApsFxState.BloodSugar.Trend, MutableList<ByteArray>>()
    private val HAMMING_DISTANCE_THRESHOLD = 4

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

            val arrowBitmaps = mutableListOf<Bitmap>()
            findImageViewBitmaps(view, arrowBitmaps)

            var detectedTrend = CamApsFxState.BloodSugar.Trend.UNKNOWN

            for (bitmap in arrowBitmaps) {
                val matchedTrend = matchTrend(bitmap)
                if (matchedTrend != CamApsFxState.BloodSugar.Trend.UNKNOWN && matchedTrend != CamApsFxState.BloodSugar.Trend.IGNORE) {
                    detectedTrend = matchedTrend
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
     * Loads all known arrow bitmaps from the external "arrows" folder, computes their pHashes, and caches them.
     */
    private fun loadAllLabeledBitmaps(context: Context) {
        Log.d(TAG, "Loading and hashing all labeled trend bitmaps from filesystem...")
        val arrowsDir = context.getExternalFilesDir("arrows")
        arrowsDir?.listFiles { _, name -> name.endsWith(".png") }?.forEach { file ->
            try {
                val trendName = file.nameWithoutExtension.substringBefore("_").uppercase()
                val trend = CamApsFxState.BloodSugar.Trend.valueOf(trendName)
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val pHash = bitmap.pHash()
                if (labeledHashes.values.flatten().none { it.contentEquals(pHash) }) {
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
     * Computes a 256-bit perceptual hash for a bitmap.
     */
    private fun Bitmap.pHash(): ByteArray {
        val size = 16
        val smallBitmap = Bitmap.createScaledBitmap(this, size, size, true)
        val hashBits = BooleanArray(size * size)
        var averageLuminance = 0.0

        val luminances = IntArray(size * size)
        for (y in 0 until size) {
            for (x in 0 until size) {
                val pixel = smallBitmap.getPixel(x, y)
                val luminance = (0.299 * Color.red(pixel) + 0.587 * Color.green(pixel) + 0.114 * Color.blue(pixel))
                luminances[y * size + x] = luminance.toInt()
                averageLuminance += luminance
            }
        }
        averageLuminance /= (size * size)

        for (i in 0 until (size * size)) {
            hashBits[i] = luminances[i] > averageLuminance
        }

        val hashBytes = ByteArray(size * size / 8)
        for (i in hashBytes.indices) {
            var byte = 0
            for (j in 0 until 8) {
                if (hashBits[i * 8 + j]) {
                    byte = byte or (1 shl (7 - j))
                }
            }
            hashBytes[i] = byte.toByte()
        }
        return hashBytes
    }

    /**
     * Calculates the Hamming distance between two pHashes (ByteArray).
     */
    private fun ByteArray.hammingDistance(other: ByteArray): Int {
        if (this.size != other.size) return Int.MAX_VALUE
        var distance = 0
        for (i in this.indices) {
            distance += Integer.bitCount((this[i].toInt() xor other[i].toInt()))
        }
        return distance
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
