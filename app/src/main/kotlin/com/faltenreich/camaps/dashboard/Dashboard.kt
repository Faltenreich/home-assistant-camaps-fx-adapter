@file:OptIn(ExperimentalMaterial3Api::class)

package com.faltenreich.camaps.dashboard

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faltenreich.camaps.MainServiceState
import com.faltenreich.camaps.R
import com.faltenreich.camaps.dashboard.log.LogList

@Composable
fun Dashboard(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.checkPermissions(context)
    }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
    ) { paddingValues ->
        when (state) {
            is DashboardState.MissingPermissions -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Button(onClick = { viewModel.openNotificationSettings(context as Activity)}) {
                    Text(stringResource(R.string.settings_open))
                }
            }
            is DashboardState.Content -> {
                Column(modifier = Modifier.padding(paddingValues)) {
                    LogList(
                        entries = state.log,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(
                                horizontal = Dimensions.Padding.P_16,
                                vertical = Dimensions.Padding.P_8,
                            ),
                    )
                    HorizontalDivider()
                    Row {
                        TextButton(
                            onClick = { viewModel.openNotificationSettings(context as Activity) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(R.string.settings_open))
                        }
                        TextButton(
                            onClick = { viewModel.toggleService() },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                stringResource(
                                    when (state.service) {
                                        is MainServiceState.Disconnected -> R.string.service_start
                                        is MainServiceState.Connected -> R.string.service_stop
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}