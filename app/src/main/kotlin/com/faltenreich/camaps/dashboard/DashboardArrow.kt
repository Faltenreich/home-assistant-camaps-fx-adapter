package com.faltenreich.camaps.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.faltenreich.camaps.Dimensions

@Composable
fun DashboardArrow(modifier: Modifier = Modifier) {
    Image(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        modifier = modifier.size(Dimensions.Size.ARROW),
    )
}