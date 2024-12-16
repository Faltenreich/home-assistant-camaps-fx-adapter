package com.faltenreich.camaps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faltenreich.camaps.adapter.BloodSugarEventAdapter

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val event = BloodSugarEventAdapter.events.collectAsStateWithLifecycle()
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(event.value?.mgDl?.toString() ?: "No event")
    }
}