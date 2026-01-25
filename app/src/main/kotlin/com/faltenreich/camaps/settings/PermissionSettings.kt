package com.faltenreich.camaps.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.faltenreich.camaps.Dimensions
import com.faltenreich.camaps.R

@Composable
fun PermissionSettings(
    state: SettingsState,
    onUpdate: (SettingsState) -> Unit,
    onOpenNotificationSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(
                horizontal = Dimensions.Padding.P_16,
                vertical = Dimensions.Padding.P_8,
            )
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.P_8),
    ) {
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimensions.Padding.P_16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusIndicator(
                status =
                    if (state.hasPermission) Status.Success(message = stringResource(R.string.permissions_success))
                    else Status.Failure(message = stringResource(R.string.permissions_failure)),
                modifier = Modifier.weight(1f),
            )
            Button(onClick = onOpenNotificationSettings) {
                Text(stringResource(R.string.settings_open))
            }
        }
    }
}