package com.faltenreich.camaps

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.faltenreich.camaps.dashboard.Dashboard
import com.faltenreich.camaps.dashboard.DashboardScreen
import com.faltenreich.camaps.settings.Settings
import com.faltenreich.camaps.settings.SettingsScreen

@Composable
fun MainScreen() {
    MaterialTheme {
        val navController = rememberNavController()

        NavHost(
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
    }
}