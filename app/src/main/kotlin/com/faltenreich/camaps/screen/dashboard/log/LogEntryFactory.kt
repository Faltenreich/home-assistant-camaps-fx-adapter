package com.faltenreich.camaps.screen.dashboard.log

import com.faltenreich.camaps.service.MainServiceState
import com.faltenreich.camaps.service.camaps.CamApsFxEvent
import com.faltenreich.camaps.service.homeassistant.HomeAssistantState
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
            is MainServiceState.MissingApp -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Missing app",
                issue = LogEntry.Issue.MISSING_APP,
            )
            is MainServiceState.MissingPermission -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Missing permission",
                issue = LogEntry.Issue.MISSING_PERMISSION,
            )
            is MainServiceState.Disconnected -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Service disconnected",
            )
            is MainServiceState.Connected -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Service connected",
            )
        }
    }

    fun create(camApsFxEvent: CamApsFxEvent): LogEntry = with(camApsFxEvent) {
        val dateTime = createDateTime()
        val source = "CamAPS FX"
        when (this) {
            is CamApsFxEvent.BloodSugar -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Notification observed: $value $unitOfMeasurement",
            )
            is CamApsFxEvent.Unknown -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Unknown observed: $message",
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
                message = "Device disconnected",
            )
            is HomeAssistantState.DeviceConnected -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Device connected",
            )
            is HomeAssistantState.SensorConnected -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Sensor connected",
            )
            is HomeAssistantState.SensorUpdated -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Sensor updated: $data"
            )
            is HomeAssistantState.Error -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Error received: $message"
            )
        }
    }
}