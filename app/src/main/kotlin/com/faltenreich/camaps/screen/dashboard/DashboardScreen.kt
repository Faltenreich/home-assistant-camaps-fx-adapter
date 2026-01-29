package com.faltenreich.camaps.screen.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faltenreich.camaps.screen.dashboard.log.LogList

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
) {
    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        is DashboardState.Loading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        is DashboardState.Content -> LogList(
            entries = state.log,
            modifier = modifier
                .fillMaxWidth()
                .fillMaxSize(),
        )
    }
}