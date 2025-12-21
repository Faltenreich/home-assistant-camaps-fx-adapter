package com.faltenreich.camaps.dashboard.log

import com.faltenreich.camaps.MainServiceState
import com.faltenreich.camaps.camaps.CamApsFxState
import java.text.SimpleDateFormat
import com.faltenreich.camaps.homeassistant.HomeAssistantData
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date
import java.util.Locale

object LogEntryFactory {
    private val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private fun createDateTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
    }

    fun create(message: String): LogEntry {
        return LogEntry(
            dateTime = simpleDateFormat.format(Date()),
            source = "System",
            message = message,
        )
    }

    fun create(serviceState: MainServiceState): LogEntry = with(serviceState) {
        val dateTime = createDateTime()
        val source = "Service"
        when (this) {
            is MainServiceState.Disconnected -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "disconnected",
            )
            is MainServiceState.Connected -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "connected",
            )
        }
    }

    fun create(camApsFxState: CamApsFxState): LogEntry? = with(camApsFxState) {
        val dateTime = createDateTime()
        val source = "CamAPS FX"
        when (this) {
            is CamApsFxState.Blank -> null
            is CamApsFxState.Off -> null
            is CamApsFxState.Starting -> null
            is CamApsFxState.Error -> null
            is CamApsFxState.BloodSugar -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Logged data: ${value} ${unitOfMeasurement} ${trend.toArrow()}",
            )
        }
    }

    fun create(homeAssistantState: HomeAssistantState): LogEntry = with(homeAssistantState) {
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