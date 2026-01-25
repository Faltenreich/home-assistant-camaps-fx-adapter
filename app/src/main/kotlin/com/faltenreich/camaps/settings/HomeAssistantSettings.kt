package com.faltenreich.camaps.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.faltenreich.camaps.Colors
import com.faltenreich.camaps.Dimensions
import com.faltenreich.camaps.R

@Composable
fun HomeAssistantSettings(
    state: SettingsState,
    onUpdate: (SettingsState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(
                horizontal = Dimensions.Padding.P_16,
                vertical = Dimensions.Padding.P_16,
            )
            .animateContentSize(),
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

        Box(contentAlignment = Alignment.Center) {
            Text(
                text = when (state.connection) {
                    is SettingsState.Connection.Loading -> ""
                    is SettingsState.Connection.Success -> stringResource(R.string.home_assistant_connection_success)
                    is SettingsState.Connection.Failure -> state.connection.message
                },
                color = when (state.connection) {
                    is SettingsState.Connection.Loading -> Color.Transparent
                    is SettingsState.Connection.Success -> Colors.Green
                    is SettingsState.Connection.Failure -> MaterialTheme.colorScheme.error
                },
                style = MaterialTheme.typography.bodySmall,
            )
            if (state.connection is SettingsState.Connection.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}