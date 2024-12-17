package com.faltenreich.camaps.dashboard.log

import androidx.compose.ui.graphics.Color
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class LogEntryFactory {

    private fun createDateTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
    }

    fun create(camApsFxState: CamApsFxState): LogEntry? = with(camApsFxState) {
        val dateTime = createDateTime()
        val source = LogEntry.Source(
            name = "CamAPS FX",
            color = Color.Green,
        )
        return when (this) {
            is CamApsFxState.None -> null
            is CamApsFxState.Value -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "received notification: ${bloodSugar.mgDl} mg/dL",
            )
            is CamApsFxState.Error -> LogEntry(
                dateTime = dateTime,
                source = source,
                message = "received error: $message",
            )
        }
    }

    fun create(homeAssistantState: HomeAssistantState): LogEntry? = with(homeAssistantState) {
        val dateTime = createDateTime()
        val source = LogEntry.Source(
            name = "Home Assistant",
            color = Color.Blue,
        )
        return when (this) {
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