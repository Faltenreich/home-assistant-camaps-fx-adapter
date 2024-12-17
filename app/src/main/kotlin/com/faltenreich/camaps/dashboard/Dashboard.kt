@file:OptIn(ExperimentalMaterial3Api::class)

package com.faltenreich.camaps.dashboard

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faltenreich.camaps.R
import com.faltenreich.camaps.dashboard.log.LogList

@Composable
fun Dashboard(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.state.collectAsStateWithLifecycle().value

    // TODO: Check onResume
    LaunchedEffect(Unit) {
        viewModel.checkNotificationListenerPermission(context)
    }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
    ) { paddingValues ->
        when (state) {
            is DashboardState.MissingNotificationListenerPermission -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Button(onClick = { viewModel.openNotificationSettings(context as Activity)}) {
                    Text(stringResource(R.string.settings_open))
                }
            }
            is DashboardState.Content -> LogList(
                entries = state.mainState.log,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(
                        horizontal = Dimensions.Padding.P_16,
                        vertical = Dimensions.Padding.P_8,
                    ),
            )
        }
    }
}