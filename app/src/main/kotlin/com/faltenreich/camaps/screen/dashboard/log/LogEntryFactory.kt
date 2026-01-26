package com.faltenreich.camaps.screen.dashboard.log

import com.faltenreich.camaps.service.MainServiceState
import com.faltenreich.camaps.service.camaps.CamApsFxState
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

    fun create(camApsFxState: CamApsFxState): LogEntry? = with(camApsFxState) {
        val dateTime = createDateTime()
        val source = "CamAPS FX"
        when (this) {
            is CamApsFxState.Blank -> null
            is CamApsFxState.BloodSugar -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Notification observed: $value $unitOfMeasurement",
            )
            is CamApsFxState.Error -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Error received: $message",
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
            is HomeAssistantState.ConnectedDevice -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Device connected",
            )
            is HomeAssistantState.ConnectedSensor -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Sensor connected",
            )
            is HomeAssistantState.UpdatedSensor -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Data updated: $data"
            )
            is HomeAssistantState.Error -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "Error received: $message"
            )
        }
    }
}