package com.faltenreich.camaps.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.faltenreich.camaps.R
import com.faltenreich.camaps.core.ui.Dimensions
import com.faltenreich.camaps.core.ui.InputField
import com.faltenreich.camaps.core.ui.Label
import com.faltenreich.camaps.core.ui.Status
import com.faltenreich.camaps.core.ui.StatusIndicator

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                navigationIcon = {
                    IconButton(onClick = navController::popBackStack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding(),
        ) {
            Label(text = stringResource(R.string.home_assistant))
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = Dimensions.Padding.P_16,
                        vertical = Dimensions.Padding.P_8,
                    )
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.P_8),
            ) {
                var uri by remember { mutableStateOf(state.uri) }
                InputField(
                    value = uri,
                    onValueChange = { input ->
                        uri = input
                        viewModel.update(state.copy(uri = input))
                    },
                    label = stringResource(R.string.home_assistant_uri),
                    hint = stringResource(R.string.home_assistant_uri_default),
                )

                var token by remember { mutableStateOf(state.token) }
                InputField(
                    value = token,
                    onValueChange = { input ->
                        token = input
                        viewModel.update(state.copy(token = input))
                    },
                    label = stringResource(R.string.home_assistant_token),
                )

                StatusIndicator(
                    status = when (val connection = state.connection) {
                        is SettingsState.Connection.Loading -> Status.Loading

                        is SettingsState.Connection.Success -> Status.Success(
                            message = stringResource(R.string.home_assistant_connection_success),
                        )

                        is SettingsState.Connection.Failure -> Status.Failure(
                            message = connection.message,
                        )
                    },
                )
            }
        }
    }
}