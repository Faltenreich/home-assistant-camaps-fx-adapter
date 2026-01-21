package com.faltenreich.camaps.settings

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.faltenreich.camaps.Dimensions
import com.faltenreich.camaps.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.checkPermission(context)
    }

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
            Label(
                text = stringResource(R.string.home_assistant),
            )
            HomeAssistant(
                state = state,
                onUpdate = viewModel::update,
                onTestConnection = viewModel::testConnection,
            )

            Label(
                text = stringResource(R.string.service),
            )
            Service(
                state = state,
                onUpdate = viewModel::update,
                onOpenNotificationSettings = { viewModel.openNotificationSettings(context as Activity) },
                onRestartService = viewModel::restartService,
                onReset = viewModel::reset,
            )
        }
    }
}

@Composable
private fun Label(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(
                horizontal = Dimensions.Padding.P_16,
                vertical = Dimensions.Padding.P_8,
            ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelLarge,
    )
}

@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    hint: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = hint?.let { { Text(hint) } },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { onValueChange("") }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                )
            }
        }
    )
}

@Composable
private fun HomeAssistant(
    state: SettingsState,
    onUpdate: (SettingsState) -> Unit,
    onTestConnection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(Dimensions.Padding.P_16),
        verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.P_16),
    ) {
        var uri by remember { mutableStateOf(state.uri) }
        InputField(
            value = uri,
            onValueChange = { input ->
                uri = input
                onUpdate(state.copy(uri = input))
            },
            label = stringResource(R.string.home_assistant_uri),
            hint = stringResource(R.string.home_assistant_uri_default),
        )

        var token by remember { mutableStateOf(state.token) }
        InputField(
            value = token,
            onValueChange = { input ->
                token = input
                onUpdate(state.copy(token = input))
            },
            label = stringResource(R.string.home_assistant_token),
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onTestConnection,
                modifier = Modifier.weight(1f)
            ) {
                Text("Test Connection")
            }
            when (val state = state.connection) {
                is SettingsState.Connection.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(start = 8.dp))
                is SettingsState.Connection.Success -> Icon(Icons.Default.Check, contentDescription = "Success", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 8.dp))
                is SettingsState.Connection.Failure -> Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Failure", tint = MaterialTheme.colorScheme.error)
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 4.dp))
                }
                is SettingsState.Connection.Idle -> Unit
            }
        }
    }
}

@Composable
private fun Service(
    state: SettingsState,
    onUpdate: (SettingsState) -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onRestartService: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(Dimensions.Padding.P_16)) {
        var notificationTimeoutMinutes by remember { mutableStateOf(state.notificationTimeoutMinutes) }
        OutlinedTextField(
            value = notificationTimeoutMinutes,
            onValueChange = { input ->
                notificationTimeoutMinutes = input
                onUpdate(state.copy(notificationTimeoutMinutes = input))
            },
            label = { Text("Notify if no readings in x minutes") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onOpenNotificationSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text("Permissions")
                Spacer(modifier = Modifier.size(8.dp))
                if (state.hasPermission) {
                    Icon(Icons.Default.Check, contentDescription = "Success", tint = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.Close, contentDescription = "Failure", tint = MaterialTheme.colorScheme.onError)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRestartService,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restart Service")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Re-register device")
        }
    }
}