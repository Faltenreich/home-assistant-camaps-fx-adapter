package com.faltenreich.camaps.dashboard.log

import androidx.compose.ui.graphics.Color
import com.faltenreich.camaps.MainServiceState
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object LogEntryFactory {

    private fun createDateTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
    }

    fun create(serviceState: MainServiceState): LogEntry = with(serviceState) {
        val dateTime = createDateTime()
        val source = LogEntry.Source(
            name = "Service",
            color = Color.Gray,
        )
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
        val source = LogEntry.Source(
            name = "CamAPS FX",
            color = Color.Green,
        )
        when (this) {
            is CamApsFxState.Blank -> null
            is CamApsFxState.BloodSugar -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "sent blood sugar: $mgDl mg/dL",
            )
            is CamApsFxState.Unknown -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "sent unknown data: $message",
            )
        }
    }

    fun create(homeAssistantState: HomeAssistantState): LogEntry = with(homeAssistantState) {
        val dateTime = createDateTime()
        val source = LogEntry.Source(
            name = "Home Assistant",
            color = Color.Blue,
        )
        when (this) {
            is HomeAssistantState.Disconnected -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "disconnected device",
            )
            is HomeAssistantState.ConnectedDevice -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "connected device",
            )
            is HomeAssistantState.ConnectedSensor -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "connected sensor",
            )
        }
    }
}