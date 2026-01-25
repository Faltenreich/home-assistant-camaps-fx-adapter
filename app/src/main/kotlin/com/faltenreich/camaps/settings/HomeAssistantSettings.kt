package com.faltenreich.camaps.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.faltenreich.camaps.Colors
import com.faltenreich.camaps.Dimensions
import com.faltenreich.camaps.R

@Composable
fun HomeAssistantSettings(
    state: SettingsState,
    onUpdate: (SettingsState) -> Unit,
    onTestConnection: () -> Unit,
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

        Button(
            onClick = onTestConnection,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.connection !is SettingsState.Connection.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = when (state.connection) {
                    is SettingsState.Connection.Idle,
                    is SettingsState.Connection.Loading -> ButtonDefaults.buttonColors().containerColor
                    is SettingsState.Connection.Success -> Colors.Green
                    is SettingsState.Connection.Failure -> MaterialTheme.colorScheme.error
                },
            ),
        ) {
            if (state.connection is SettingsState.Connection.Loading) {
                LinearProgressIndicator()
            } else {
                Text(stringResource(R.string.home_assistant_connection_test))
            }
        }

        if (state.connection is SettingsState.Connection.Success) {
            Text(
                text = stringResource(R.string.home_assistant_connection_success),
                color = Colors.Green,
                style = MaterialTheme.typography.bodySmall,
            )
        } else if (state.connection is SettingsState.Connection.Failure) {
            Text(
                text = state.connection.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}