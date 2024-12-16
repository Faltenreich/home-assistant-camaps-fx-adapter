package com.faltenreich.camaps.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faltenreich.camaps.dashboard.camaps.CamApsFxDashboard
import com.faltenreich.camaps.dashboard.homeassistant.HomeAssistantDashboard

@Composable
fun Dashboard(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HomeAssistantDashboard(
            state = state.homeAssistant,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )
        CamApsFxDashboard(
            state = state.camApsFx,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )
    }
}