package com.faltenreich.camaps.dashboard.log

import com.faltenreich.camaps.MainServiceState
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantData
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import com.faltenreich.camaps.camaps.toArrow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object LogEntryFactory {
    private fun createDateTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
    }

    fun create(message: String): LogEntry {
        return LogEntry(
            dateTime = createDateTime(),
            source = "System",
            message = message,
        )
    }

    fun create(serviceState: MainServiceState): LogEntry? = with(serviceState) {
        val dateTime = createDateTime()
        val source = "Service"
        when (this) {
            is MainServiceState.Disconnected -> null
            is MainServiceState.Connected -> null
        }
    }

    fun create(camApsFxState: CamApsFxState): LogEntry? = with(camApsFxState) {
        val dateTime = createDateTime()
        val source = "CamAPS FX"
        // TODO: do we want to do anything with this later on
        when (this) {
            is CamApsFxState.Blank -> null
            is CamApsFxState.Off -> null
            is CamApsFxState.Starting -> null
            is CamApsFxState.Error -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Error: $message",
            )
            is CamApsFxState.BloodSugar -> null
        }
    }

    fun create(homeAssistantState: HomeAssistantState): LogEntry? = with(homeAssistantState) {
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
            dateTime = createDateTime(),
            source = "Home Assistant",
            message = message,
        )
    }
}