package com.faltenreich.camaps.dashboard.log

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.faltenreich.camaps.Dimensions

@Composable
fun LogList(
    entries: List<LogEntry>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(entries) {
        if (entries.isNotEmpty()) {
            listState.scrollToItem(entries.lastIndex)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(
            horizontal = Dimensions.Padding.P_16,
            vertical = Dimensions.Padding.P_8,
        ),
    ) {
        items(entries) { entry ->
            LogListItem(entry)
        }
    }
}