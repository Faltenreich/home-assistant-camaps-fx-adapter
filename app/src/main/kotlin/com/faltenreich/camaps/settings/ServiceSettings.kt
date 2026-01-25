package com.faltenreich.camaps.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.faltenreich.camaps.Dimensions

@Composable
fun ServiceSettings(
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