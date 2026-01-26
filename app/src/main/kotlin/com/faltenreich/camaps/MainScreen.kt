package com.faltenreich.camaps

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.faltenreich.camaps.screen.dashboard.Dashboard
import com.faltenreich.camaps.screen.dashboard.DashboardScreen
import com.faltenreich.camaps.screen.settings.Settings
import com.faltenreich.camaps.screen.settings.SettingsScreen

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
) {
    MaterialTheme {
        val state = viewModel.state.collectAsStateWithLifecycle().value
        val context = LocalContext.current
        val navController = rememberNavController()

        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            viewModel.checkPermissions(context)
        }

        when (state.permission) {
            is MainState.Permission.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is MainState.Permission.Granted -> NavHost(
                navController = navController,
                startDestination = Dashboard,
                modifier = Modifier.imePadding(),
            ) {
                composable<Dashboard> {
                    DashboardScreen(navController)
                }
                composable<Settings> {
                    SettingsScreen(navController)
                }
            }

            is MainState.Permission.Denied -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Button(onClick = { viewModel.openNotificationSettings(context as Activity)}) {
                    Text(stringResource(R.string.settings_open))
                }
            }
        }
    }
}