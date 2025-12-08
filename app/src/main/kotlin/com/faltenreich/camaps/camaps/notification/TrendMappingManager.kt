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
    // Cache for pHashes, keyed by the size they were rendered at.
    private val cachedHashes = mutableMapOf<Int, Map<CamApsFxState.BloodSugar.Trend, List<ByteArray>>>()
    private var cachedIconSize = 0
    private const val HAMMING_DISTANCE_THRESHOLD = 1

    /**
     * Matches a bitmap from a notification against our cached, size-matched drawable hashes.
     */
    fun matchTrend(bitmap: Bitmap, context: Context): CamApsFxState.BloodSugar.Trend {
        // If the notification icon size changes, we must invalidate our cache and re-render.
        if (cachedIconSize != bitmap.width) {
            Log.d(TAG, "Notification icon size changed from $cachedIconSize to ${bitmap.width}. Re-rendering hashes.")
            cachedHashes.clear()
            cachedIconSize = bitmap.width
        }

        // If the cache is empty for this size, build it.
        if (cachedHashes[cachedIconSize] == null) {
            buildHashCacheForSize(context, cachedIconSize)
        }

        val hashesForSize = cachedHashes[cachedIconSize] ?: return CamApsFxState.BloodSugar.Trend.UNKNOWN
        val extractedHash = bitmap.pHash()

        // A hash of all 0s or all 1s indicates a blank/uniform image. These can cause false positives.
        val allZeros = extractedHash.all { it == 0.toByte() }
        val allOnes = extractedHash.all { it == (-1).toByte() } // -1 is all bits set to 1 in a signed byte
        if (allZeros || allOnes) {
            Log.d(TAG, "Bitmap appears to be blank (pHash is all 0s or 1s), skipping trend matching.")
            return CamApsFxState.BloodSugar.Trend.UNKNOWN
        }

        for ((trend, hashes) in hashesForSize) {
            if (hashes.any { labeledHash -> labeledHash.hammingDistance(extractedHash) <= HAMMING_DISTANCE_THRESHOLD }) {
                return trend
            }
        }

        return CamApsFxState.BloodSugar.Trend.UNKNOWN
    }

    /**
     * Renders all our vector drawables to a specific size, computes their pHashes, and caches the results.
     */
    private fun buildHashCacheForSize(context: Context, size: Int) {
        Log.d(TAG, "Building pHash cache for icon size: $size")
        val newHashes = mutableMapOf<CamApsFxState.BloodSugar.Trend, MutableList<ByteArray>>()
        for (trend in CamApsFxState.BloodSugar.Trend.values()) {
            for (resId in trend.imageResourceIds) {
                try {
                    val drawable = ContextCompat.getDrawable(context, resId)
                    if (drawable != null) {
                        // Render the vector to the EXACT size of the notification icon
                        val bitmap = drawable.toBitmap(size, size)
                        val pHash = bitmap.pHash()
                        newHashes.getOrPut(trend) { mutableListOf() }.add(pHash)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Could not load or hash drawable resource for trend: $trend")
                }
            }
        }
        cachedHashes[size] = newHashes
    }

    private fun Bitmap.pHash(): ByteArray {
        // 1. Crop to the center 50% of the image.
        val cropWidth = width / 2
        val cropHeight = height / 2
        val startX = width / 4
        val startY = height / 4
        val croppedBitmap = Bitmap.createBitmap(this, startX, startY, cropWidth, cropHeight)

        // 2. Resize the cropped image to a small, fixed size (12x12).
        val size = 12
        val smallBitmap = Bitmap.createScaledBitmap(croppedBitmap, size, size, true)
        val hashBits = BooleanArray(size * size)
        var averageLuminance = 0.0

        // 3. Convert to grayscale and calculate the average pixel value.
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

        // 4. For each pixel, create a bit: 1 if brighter than average, 0 if darker.
        for (i in 0 until (size * size)) {
            hashBits[i] = luminances[i] > averageLuminance
        }

        // 5. Pack the bits into a ByteArray.
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

    private fun ByteArray.hammingDistance(other: ByteArray): Int {
        if (this.size != other.size) return Int.MAX_VALUE
        var distance = 0
        for (i in this.indices) {
            distance += Integer.bitCount((this[i].toInt() xor other[i].toInt()))
        }
        return distance
    }
}
