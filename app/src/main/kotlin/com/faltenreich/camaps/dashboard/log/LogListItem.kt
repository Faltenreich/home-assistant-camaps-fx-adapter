package com.faltenreich.camaps.dashboard.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.faltenreich.camaps.dashboard.Dimensions

@Composable
fun LogListItem(
    entry: LogEntry,
    modifier: Modifier = Modifier,
) = with(entry) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Padding.P_4),
        verticalAlignment = Alignment.Top,
    ) {
        Text(dateTime)
        Text(source, fontWeight = FontWeight.Bold)
        Text(message)
    }
}