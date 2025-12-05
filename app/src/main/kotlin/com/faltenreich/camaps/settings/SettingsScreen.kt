package com.faltenreich.camaps.settings

import android.app.Activity
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
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
    val unitType by viewModel.unitType.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    val unitTypes = listOf("mmol/L", "mg/dL")

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
            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = unitType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unit Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    unitTypes.forEach { unitType ->
                        DropdownMenuItem(
                            text = { Text(unitType) },
                            onClick = {
                                viewModel.onUnitTypeChanged(unitType)
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.openNotificationSettings(context as Activity) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Permissions")
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
