package com.faltenreich.camaps.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.faltenreich.camaps.core.ui.Dimensions
import com.faltenreich.camaps.R
import com.faltenreich.camaps.core.ui.InputField
import com.faltenreich.camaps.core.ui.Status
import com.faltenreich.camaps.core.ui.StatusIndicator

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

        StatusIndicator(
            status = when (state.connection) {
                is SettingsState.Connection.Loading -> Status.Loading
                is SettingsState.Connection.Success -> Status.Success(
                    message = stringResource(R.string.home_assistant_connection_success),
                )

                is SettingsState.Connection.Failure -> Status.Failure(
                    message = state.connection.message,
                )
            },
        )
    }
}