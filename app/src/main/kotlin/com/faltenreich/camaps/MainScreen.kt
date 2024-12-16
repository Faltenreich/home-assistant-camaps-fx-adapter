package com.faltenreich.camaps

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantState

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val camApsFxState = StateHolder.camApsFxState.collectAsStateWithLifecycle()
    val homeAssistantState = StateHolder.homeAssistantState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Home Assistant",
                modifier = Modifier.weight(1f),
            )
            when (homeAssistantState.value) {
                is HomeAssistantState.Disconnected -> Text("Disconnected")
                is HomeAssistantState.ConnectedDevice -> Text("Device connected")
                is HomeAssistantState.ConnectedSensor -> Text("Sensor connected")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "CamAPS FX",
                modifier = Modifier.weight(1f),
            )
            when (val state = camApsFxState.value) {
                is CamApsFxState.None -> Text("-")
                is CamApsFxState.Value -> {
                    Text(state.bloodSugar.mgDl.toString())
                    state.bloodSugar.trend?.let { trend ->
                        Image(
                            imageVector = trend.imageVector,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(trend.color),
                        )
                    }
                }
                is CamApsFxState.Error -> Text("ERROR: ${state.message}")
            }
        }
    }
}