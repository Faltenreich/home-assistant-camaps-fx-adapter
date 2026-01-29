package com.faltenreich.camaps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faltenreich.camaps.core.ui.Theme
import com.faltenreich.camaps.screen.dashboard.DashboardScreen
import com.faltenreich.camaps.screen.login.LoginScreen

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    Theme {
        val state = viewModel.state.collectAsStateWithLifecycle().value

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            },
        ) { paddingValues ->

            when (state) {
                is MainState.Loading -> Box(
                    modifier = Modifier.padding(paddingValues).fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

                is MainState.Authenticated -> DashboardScreen(
                    modifier = Modifier.padding(paddingValues).fillMaxSize(),
                )

                is MainState.Unauthenticated -> LoginScreen(
                    modifier = Modifier.padding(paddingValues).fillMaxSize(),
                )
            }
        }
    }
}