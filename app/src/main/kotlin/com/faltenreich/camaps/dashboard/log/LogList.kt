package com.faltenreich.camaps.dashboard.log

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LogList(
    entries: List<LogEntry>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(entries) { entry ->
            Text("${entry.dateTime}: ${entry.message}")
        }
    }
}