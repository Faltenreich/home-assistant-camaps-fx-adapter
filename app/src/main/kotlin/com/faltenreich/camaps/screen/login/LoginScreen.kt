package com.faltenreich.camaps.screen.login

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faltenreich.camaps.R
import com.faltenreich.camaps.core.ui.Dimensions
import com.faltenreich.camaps.core.ui.Status
import com.faltenreich.camaps.core.ui.StatusIndicator

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Box(
            modifier = Modifier.safeContentPadding(),
            contentAlignment = Alignment.Center,
        ) {
            Card {
                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = Dimensions.Padding.P_16,
                            vertical = Dimensions.Padding.P_8,
                        )
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(
                        space = Dimensions.Padding.P_8,
                        alignment = Alignment.CenterVertically,
                    ),
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
        }
    }
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
        },
    )
}