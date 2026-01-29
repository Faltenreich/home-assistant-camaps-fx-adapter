package com.faltenreich.camaps.screen.dashboard

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.faltenreich.camaps.R
import com.faltenreich.camaps.screen.dashboard.log.LogList

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.checkPermissions(context)
    }

    when (state) {
        is DashboardState.Loading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        is DashboardState.MissingPermission -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Button(onClick = { viewModel.openNotificationSettings(context as Activity)}) {
                Text(stringResource(R.string.settings_open))
            }
        }
        is DashboardState.Content -> LogList(
            entries = state.log,
            modifier = modifier.fillMaxSize(),
        )
    }
}