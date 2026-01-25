package com.faltenreich.camaps.screen.dashboard.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.faltenreich.camaps.core.ui.Dimensions
import com.faltenreich.camaps.core.ui.Label

@Composable
fun LogListItem(
    entry: LogEntry,
    modifier: Modifier = Modifier,
) = with(entry) {
    Column(modifier = modifier) {
        Label {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.Padding.P_4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = source,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = dateTime,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
        Text(
            text = message,
            modifier = Modifier.padding(
                horizontal = Dimensions.Padding.P_16,
                vertical = Dimensions.Padding.P_8,
            ),
        )
    }
}