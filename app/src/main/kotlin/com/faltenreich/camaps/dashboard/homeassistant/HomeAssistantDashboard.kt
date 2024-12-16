package com.faltenreich.camaps.dashboard.homeassistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.faltenreich.camaps.homeassistant.HomeAssistantState

@Composable
fun HomeAssistantDashboard(
    state: HomeAssistantState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.background(
            when (state) {
                is HomeAssistantState.Disconnected -> Color.Red
                is HomeAssistantState.ConnectedDevice -> Color.Yellow
                is HomeAssistantState.ConnectedSensor -> Color.Green
            }
        ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Home Assistant",
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Preview
@Composable
private fun Preview(
    @PreviewParameter(HomeAssistantStatePreviewParameterProvider::class)
    state: HomeAssistantState,
) {
    HomeAssistantDashboard(state)
}