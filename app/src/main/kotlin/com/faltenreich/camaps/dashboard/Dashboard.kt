package com.faltenreich.camaps.dashboard

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faltenreich.camaps.camaps.BloodSugar
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantState

@Composable
fun Dashboard(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

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
            when (state.value.homeAssistantState) {
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
            when (val state = state.value.camApsFxState) {
                is CamApsFxState.None -> Text("-")
                is CamApsFxState.Value -> {
                    Text(state.bloodSugar.mgDl.toString())
                    Text(
                        when (state.bloodSugar.trend) {
                            BloodSugar.Trend.RISING_FAST -> "+++"
                            BloodSugar.Trend.RISING -> "++"
                            BloodSugar.Trend.RISING_SLOW -> "+"
                            BloodSugar.Trend.STEADY -> ""
                            BloodSugar.Trend.DROPPING_SLOW -> "-"
                            BloodSugar.Trend.DROPPING -> "--"
                            BloodSugar.Trend.DROPPING_FAST -> "---"
                            null -> ""
                        }
                    )
                }
                is CamApsFxState.Error -> Text("ERROR: ${state.message}")
            }
        }
    }
}