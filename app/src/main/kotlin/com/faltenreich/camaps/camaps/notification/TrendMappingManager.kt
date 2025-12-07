package com.faltenreich.camaps.camaps.notification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.faltenreich.camaps.camaps.CamApsFxState
import java.io.File
import java.io.FileOutputStream

object TrendMappingManager {

    private val TAG = TrendMappingManager::class.java.simpleName
    private val labeledHashes = mutableMapOf<CamApsFxState.BloodSugar.Trend, MutableList<ByteArray>>()
    private const val HAMMING_DISTANCE_THRESHOLD = 4

    fun invalidate() {
        labeledHashes.clear()
    }

    fun matchTrend(bitmap: Bitmap, context: Context): CamApsFxState.BloodSugar.Trend {
        if (labeledHashes.isEmpty()) {
            loadAllLabeledBitmaps(context)
        }
        val extractedHash = bitmap.pHash()
        for ((trend, hashes) in labeledHashes) {
            if (hashes.any { labeledHash -> labeledHash.hammingDistance(extractedHash) <= HAMMING_DISTANCE_THRESHOLD }) {
                return trend
            }
        }
        return CamApsFxState.BloodSugar.Trend.UNKNOWN
    }

    fun saveNewBitmap(context: Context, bitmap: Bitmap) {
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

    internal fun parseTrendFromFileName(fileName: String): CamApsFxState.BloodSugar.Trend {
        val cleanFileName = fileName.lowercase()
        var longestMatch = ""
        var matchedTrend = CamApsFxState.BloodSugar.Trend.UNKNOWN

        for (trendEnum in CamApsFxState.BloodSugar.Trend.values()) {
            val trendPrefix = trendEnum.name.lowercase()
            if (cleanFileName.startsWith(trendPrefix)) {
                if (trendPrefix.length > longestMatch.length) {
                    longestMatch = trendPrefix
                    matchedTrend = trendEnum
                }
            }
        }
        return matchedTrend
    }

    private fun loadAllLabeledBitmaps(context: Context) {
        Log.d(TAG, "Loading and hashing all labeled trend bitmaps from filesystem...")
        val arrowsDir = context.getExternalFilesDir("arrows")
        arrowsDir?.listFiles { _, name -> name.endsWith(".png") }?.forEach { file ->
            try {
                val trend = parseTrendFromFileName(file.nameWithoutExtension)
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val pHash = bitmap.pHash()
                if (labeledHashes.values.flatten().none { it.contentEquals(pHash) }) {
                    labeledHashes.getOrPut(trend) { mutableListOf() }.add(pHash)
                    Log.d(TAG, "Loaded user-labeled trend from file: ${file.name}")
                }
            } catch (e: Exception) { /* Ignore improperly named files */ }
        }
    }

    private fun Bitmap.pHash(): ByteArray {
        val cropWidth = width / 2
        val cropHeight = height / 2
        val startX = width / 4
        val startY = height / 4
        val croppedBitmap = Bitmap.createBitmap(this, startX, startY, cropWidth, cropHeight)

        val size = 16
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

    private fun ByteArray.hammingDistance(other: ByteArray): Int {
        if (this.size != other.size) return Int.MAX_VALUE
        var distance = 0
        for (i in this.indices) {
            distance += Integer.bitCount((this[i].toInt() xor other[i].toInt()))
        }
        return distance
    }
}
