package com.faltenreich.camaps.dashboard.log

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantData
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LogEntry(
    val dateTime: String,
    val source: String,
    val message: String,
)
