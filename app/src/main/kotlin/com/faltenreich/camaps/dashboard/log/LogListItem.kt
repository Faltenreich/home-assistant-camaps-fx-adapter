package com.faltenreich.camaps.dashboard.log

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.faltenreich.camaps.Dimensions

@Composable
fun LogListItem(
    entry: LogEntry,
    modifier: Modifier = Modifier,
) = with(entry) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(Dimensions.Padding.P_4),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.Padding.P_4),
            verticalAlignment = Alignment.Top,
        ) {
            Text(source, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(dateTime, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(message, modifier = Modifier.padding(Dimensions.Padding.P_4))
    }
}