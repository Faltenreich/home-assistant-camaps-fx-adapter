package com.faltenreich.camaps.camaps.notification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.faltenreich.camaps.camaps.CamApsFxState

object TrendMappingManager {

    private val TAG = TrendMappingManager::class.java.simpleName
    private val cachedHashes = mutableMapOf<Int, Map<CamApsFxState.BloodSugar.Trend, List<ByteArray>>>()
    private var cachedIconSize = 0
    private const val HAMMING_DISTANCE_THRESHOLD = 1

    fun matchTrend(bitmap: Bitmap, context: Context): CamApsFxState.BloodSugar.Trend {
        if (cachedIconSize != bitmap.width) {
            cachedHashes.clear()
            cachedIconSize = bitmap.width
        }

        if (cachedHashes[cachedIconSize] == null) {
            buildHashCacheForSize(context, cachedIconSize)
        }

        val hashesForSize = cachedHashes[cachedIconSize] ?: return CamApsFxState.BloodSugar.Trend.UNKNOWN
        val extractedHash = bitmap.pHash()

        val allZeros = extractedHash.all { it == 0.toByte() }
        val allOnes = extractedHash.all { it == (-1).toByte() }
        if (allZeros || allOnes) {
            return CamApsFxState.BloodSugar.Trend.UNKNOWN
        }

        for ((trend, hashes) in hashesForSize) {
            if (hashes.any { labeledHash -> labeledHash.hammingDistance(extractedHash) <= HAMMING_DISTANCE_THRESHOLD }) {
                return trend
            }
        }

        return CamApsFxState.BloodSugar.Trend.UNKNOWN
    }

    private fun buildHashCacheForSize(context: Context, size: Int) {
        Log.d(TAG, "Building pHash cache for icon size: $size")
        val newHashes = mutableMapOf<CamApsFxState.BloodSugar.Trend, MutableList<ByteArray>>()
        for (trend in CamApsFxState.BloodSugar.Trend.entries) {
            for (resId in trend.imageResourceIds) {
                try {
                    val drawable = ContextCompat.getDrawable(context, resId)
                    if (drawable != null) {
                        val bitmap = drawable.toBitmap(size, size)
                        val pHash = bitmap.pHash()
                        newHashes.getOrPut(trend) { mutableListOf() }.add(pHash)
                    }
                } catch (_: Exception) {
                    Log.w(TAG, "Could not load or hash drawable resource for trend: $trend")
                }
            }
        }
        cachedHashes[size] = newHashes
    }

    /*
    Convert our image to a perceptual hash for comparison
    - crop the center 50% of the image containing the arrow
    - resize to 12x12 pixels to reduce the comparison size
    - Convert pixels to luminance values
    - Compare each pixel to the average luminance
    - Store the resulting binary values
     */
    private fun Bitmap.pHash(): ByteArray {
        val cropWidth = width / 2
        val cropHeight = height / 2
        val startX = width / 4
        val startY = height / 4
        val croppedBitmap = Bitmap.createBitmap(this, startX, startY, cropWidth, cropHeight)

        val size = 12
        val smallBitmap = Bitmap.createScaledBitmap(croppedBitmap, size, size, true)
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

    /*
    Compare two hashes by counting the number of different bits
     */
    private fun ByteArray.hammingDistance(other: ByteArray): Int {
        if (this.size != other.size) return Int.MAX_VALUE
        var distance = 0
        for (i in this.indices) {
            distance += Integer.bitCount((this[i].toInt() xor other[i].toInt()))
        }
        return distance
    }
}
