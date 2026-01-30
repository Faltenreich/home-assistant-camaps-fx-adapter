package com.faltenreich.camaps.screen.dashboard.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.faltenreich.camaps.R
import com.faltenreich.camaps.core.ui.Dimensions
import com.faltenreich.camaps.core.ui.Label

@Composable
fun LogListItem(
    entry: LogEntry,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) = with(entry) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Dimensions.Padding.P_16,
                    vertical = Dimensions.Padding.P_8,
                ),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.Padding.P_4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
            )
            when (entry.issue) {
                LogEntry.Issue.MISSING_PERMISSION -> ElevatedButton(onClick = onOpenSettings) {
                    Text(stringResource(R.string.settings_open))
                }
                null -> Unit
            }
        }
    }
}