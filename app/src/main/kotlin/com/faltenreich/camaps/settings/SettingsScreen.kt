package com.faltenreich.camaps.settings

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(),
) {
    val context = LocalContext.current
    val uri by viewModel.uri.collectAsState()
    val token by viewModel.token.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val hasPermission by viewModel.hasPermission.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.checkPermission(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uri,
                onValueChange = viewModel::onUriChanged,
                label = { Text("Home Assistant URI") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = token,
                onValueChange = viewModel::onTokenChanged,
                label = { Text("Long-Lived Token") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = viewModel::testConnection,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Test Connection")
                }
                when (val state = connectionState) {
                    ConnectionState.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(start = 8.dp))
                    ConnectionState.Success -> Icon(Icons.Default.Check, contentDescription = "Success", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 8.dp))
                    is ConnectionState.Failure -> Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Failure", tint = MaterialTheme.colorScheme.error)
                        Text(text = state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 4.dp))
                    }
                    ConnectionState.Idle -> { /* Do nothing */ }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.openNotificationSettings(context as Activity) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text("Permissions")
                    Spacer(modifier = Modifier.size(8.dp))
                    if (hasPermission) {
                        Icon(Icons.Default.Check, contentDescription = "Success", tint = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Default.Close, contentDescription = "Failure", tint = MaterialTheme.colorScheme.onError)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = viewModel::restartService,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Restart Service")
            }
        }
    }
}
