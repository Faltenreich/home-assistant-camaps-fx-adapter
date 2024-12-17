package com.faltenreich.camaps.dashboard.log

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import java.time.LocalDateTime

class LogEntryFactory {

    fun create(camApsFxState: CamApsFxState): LogEntry? = with(camApsFxState) {
        return when (this) {
            is CamApsFxState.None -> null
            is CamApsFxState.Value -> LogEntry(
                dateTime = LocalDateTime.now(),
                message = "CamAPS FX updated value: ${bloodSugar.mgDl} mg/dL",
            )
            is CamApsFxState.Error -> LogEntry(
                dateTime = LocalDateTime.now(),
                message = "CamAPS FX received error: $message",
            )
        }
    }

    fun create(homeAssistantState: HomeAssistantState): LogEntry? = with(homeAssistantState) {
        return when (this) {
            is HomeAssistantState.Disconnected -> null
            is HomeAssistantState.ConnectedDevice -> LogEntry(
                dateTime = LocalDateTime.now(),
                message = "HomeAssistant connected device",
            )
            is HomeAssistantState.ConnectedSensor -> LogEntry(
                dateTime = LocalDateTime.now(),
                message = "HomeAssistant connected sensor",
            )
        }
    }
}