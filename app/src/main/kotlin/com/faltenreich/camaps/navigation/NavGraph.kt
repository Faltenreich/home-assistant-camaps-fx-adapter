package com.faltenreich.camaps.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.faltenreich.camaps.dashboard.Dashboard
import com.faltenreich.camaps.settings.SettingsScreen
import com.faltenreich.camaps.settings.arrowmapping.ArrowMappingScreen

@Composable
fun NavGraph() {
    Log.d("NavGraph", "Composing NavGraph")
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            Dashboard(onSettingsClick = { navController.navigate("settings") })
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToArrowMapping = { navController.navigate("arrow_mapping") }
            )
        }
        composable("arrow_mapping") {
            ArrowMappingScreen(onBack = { navController.popBackStack() })
        }
    }
}