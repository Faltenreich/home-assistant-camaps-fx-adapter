package com.faltenreich.camaps.settings.arrowmapping

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.camaps.notification.TrendMappingManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class ArrowMappingViewModel(application: Application) : AndroidViewModel(application) {

    private val _arrowMappings = MutableStateFlow<List<ArrowMapping>>(emptyList())
    val arrowMappings = _arrowMappings.asStateFlow()

    init {
        loadArrowMappings()
    }

    private fun loadArrowMappings() {
        val context = getApplication<Application>().applicationContext
        val arrowsDir = context.getExternalFilesDir("arrows")
        val mappings = mutableListOf<ArrowMapping>()

        arrowsDir?.listFiles { _, name -> name.endsWith(".png") }?.forEach { file ->
            // Use the centralized, correct parser
            val trend = TrendMappingManager.parseTrendFromFileName(file.nameWithoutExtension)
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            mappings.add(ArrowMapping(file, bitmap, trend))
        }

        _arrowMappings.value = mappings.sortedBy { it.assignedTrend.ordinal }
    }

    fun onTrendSelected(mapping: ArrowMapping, newTrend: CamApsFxState.BloodSugar.Trend) {
        val newFileNamePrefix = when (newTrend) {
            CamApsFxState.BloodSugar.Trend.UNKNOWN -> "unknown"
            CamApsFxState.BloodSugar.Trend.IGNORE -> "ignore"
            else -> newTrend.name.lowercase()
        }
        val newFileName = "${newFileNamePrefix}_${System.currentTimeMillis()}.png"

        val newFile = File(mapping.file.parent, newFileName)
        if (mapping.file.renameTo(newFile)) {
            TrendMappingManager.invalidate()
            loadArrowMappings() // Reload to reflect the change
        }
    }
}

data class ArrowMapping(
    val file: File,
    val bitmap: Bitmap,
    val assignedTrend: CamApsFxState.BloodSugar.Trend
)
