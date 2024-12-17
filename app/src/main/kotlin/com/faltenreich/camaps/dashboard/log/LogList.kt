package com.faltenreich.camaps.dashboard.log

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.faltenreich.camaps.Dimensions

@Composable
fun LogList(
    entries: List<LogEntry>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.padding(all = Dimensions.Padding.P_8)) {
        items(entries) { entry ->
            LogListItem(entry)
        }
    }
}