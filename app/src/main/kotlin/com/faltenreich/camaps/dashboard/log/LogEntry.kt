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
) {
    companion object {
        private val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        fun from(homeAssistantState: HomeAssistantState): LogEntry {
            val message = when (homeAssistantState) {
                is HomeAssistantState.ConnectedDevice -> homeAssistantState.message
                is HomeAssistantState.ConnectedSensor -> homeAssistantState.message
                is HomeAssistantState.Disconnected -> "Disconnected from Home Assistant"
                is HomeAssistantState.Error -> homeAssistantState.message
                is HomeAssistantState.Idle -> "Home Assistant is idle"
                is HomeAssistantState.UpdatedSensor -> {
                    val bloodSugar = homeAssistantState.data as? HomeAssistantData.BloodSugar
                    if (bloodSugar != null) {
                        "Sensor updated: ${bloodSugar.value} ${bloodSugar.unitOfMeasurement} ${bloodSugar.trend.toArrow()}".trim()
                    } else {
                        "Sensor updated with unknown data"
                    }
                }
            }
            return LogEntry(
                dateTime = simpleDateFormat.format(Date()),
                source = "Home Assistant",
                message = message,
            )
        }

        fun system(message: String): LogEntry {
            return LogEntry(
                dateTime = simpleDateFormat.format(Date()),
                source = "System",
                message = message,
            )
        }

        private fun CamApsFxState.BloodSugar.Trend?.toArrow(): String = when (this) {
            CamApsFxState.BloodSugar.Trend.RISING_FAST -> "↟"
            CamApsFxState.BloodSugar.Trend.RISING -> "↑"
            CamApsFxState.BloodSugar.Trend.RISING_SLOW -> "↗"
            CamApsFxState.BloodSugar.Trend.STEADY -> "→"
            CamApsFxState.BloodSugar.Trend.DROPPING_SLOW -> "↘"
            CamApsFxState.BloodSugar.Trend.DROPPING -> "↓"
            CamApsFxState.BloodSugar.Trend.DROPPING_FAST -> "↡"
            else -> ""
        }
    }
}
