package com.faltenreich.camaps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faltenreich.camaps.core.ui.Theme
import com.faltenreich.camaps.screen.dashboard.DashboardScreen
import com.faltenreich.camaps.screen.login.LoginScreen

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    Theme {
        when (viewModel.state.collectAsStateWithLifecycle().value) {
            is MainState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is MainState.Unauthenticated -> LoginScreen(
                modifier = Modifier.fillMaxSize(),
            )

            is MainState.Authenticated -> DashboardScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}