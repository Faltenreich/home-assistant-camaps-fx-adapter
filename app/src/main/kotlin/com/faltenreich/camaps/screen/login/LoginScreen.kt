package com.faltenreich.camaps.screen.login

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faltenreich.camaps.R
import com.faltenreich.camaps.core.ui.Dimensions
import com.faltenreich.camaps.core.ui.InputField
import com.faltenreich.camaps.core.ui.Status
import com.faltenreich.camaps.core.ui.StatusIndicator

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = Dimensions.Padding.P_16,
                vertical = Dimensions.Padding.P_8,
            )
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.P_8),
    ) {
        InputField(
            value = viewModel.uri,
            onValueChange = { viewModel.uri = it },
            label = stringResource(R.string.home_assistant_uri),
            hint = stringResource(R.string.home_assistant_uri_default),
        )

        InputField(
            value = viewModel.token,
            onValueChange = { viewModel.token = it },
            label = stringResource(R.string.home_assistant_token),
        )

        Button(
            onClick = viewModel::confirm,
            enabled = state.connection is LoginState.Connection.Success,
        ) {
            Text(stringResource(R.string.confirm))
        }

        StatusIndicator(
            status = when (val connection = state.connection) {
                is LoginState.Connection.Idle -> Status.None

                is LoginState.Connection.Loading -> Status.Loading

                is LoginState.Connection.Success -> Status.Success(
                    message = stringResource(R.string.home_assistant_connection_success),
                )

                is LoginState.Connection.Failure -> Status.Failure(
                    message = connection.message,
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}