package com.faltenreich.camaps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantState

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val state = MainStateObserver.state.collectAsStateWithLifecycle().value
    val camApsFxState = state.camApsFxState
    val homeAssistantState = state.homeAssistantState

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "CamAPS FX",
                modifier = Modifier.weight(1f),
            )
            when (camApsFxState) {
                is CamApsFxState.None -> Text("No event")
                is CamApsFxState.Value -> Text(camApsFxState.bloodSugar.mgDl.toString())
                is CamApsFxState.Error -> Text("ERROR: ${camApsFxState.message}")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Home Assistant",
                modifier = Modifier.weight(1f),
            )
            when (homeAssistantState) {
                is HomeAssistantState.Disconnected -> Text("Disconnected")
                is HomeAssistantState.ConnectedDevice -> Text("Device connected")
                is HomeAssistantState.ConnectedSensor -> Text("Sensor connected")
            }
        }
    }
}