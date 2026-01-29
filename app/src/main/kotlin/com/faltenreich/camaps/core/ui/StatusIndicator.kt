package com.faltenreich.camaps.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun StatusIndicator(
    status: Status,
    modifier: Modifier = Modifier,
) {
    val color = when (status) {
        is Status.None,
        is Status.Loading -> Color.Transparent
        is Status.Success -> Colors.Green
        is Status.Failure -> MaterialTheme.colorScheme.error
    }
    val icon = when (status) {
        is Status.None,
        is Status.Loading -> Icons.Default.Refresh
        is Status.Success -> Icons.Default.Check
        is Status.Failure -> Icons.Default.Clear
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimensions.Padding.P_16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
            )
            Text(
                text = when (status) {
                    is Status.None,
                    is Status.Loading -> ""
                    is Status.Success -> status.message
                    is Status.Failure -> status.message
                },
                color = color,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (status is Status.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}