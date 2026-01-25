package com.faltenreich.camaps.dashboard.log

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
            is CamApsFxState.BloodSugar -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "sent data: $value $unitOfMeasurement",
            )
            is CamApsFxState.Error -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "sent something: $message",
            )
        }
    }

    fun create(homeAssistantState: HomeAssistantState): LogEntry = with(homeAssistantState) {
        val dateTime = createDateTime()
        val source = "Home Assistant"
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
            is HomeAssistantState.UpdatedSensor -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "sent data: $data"
            )
            is HomeAssistantState.Error -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "received error: $message"
            )
        }
    }

    @Deprecated("Replace with state-bound methods")
    fun create(message: String): LogEntry {
        return LogEntry(
            dateTime = createDateTime(),
            source = "System",
            message = message,
        )
    }
}