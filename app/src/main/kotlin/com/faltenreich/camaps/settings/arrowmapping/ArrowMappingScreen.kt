package com.faltenreich.camaps.settings.arrowmapping

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faltenreich.camaps.camaps.CamApsFxState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArrowMappingScreen(
    onBack: () -> Unit,
    viewModel: ArrowMappingViewModel = viewModel()
) {
    val mappings by viewModel.arrowMappings.collectAsState()
    val (assigned, unassigned) = mappings.partition { it.assignedTrend != CamApsFxState.BloodSugar.Trend.UNKNOWN }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map Trending Arrows") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Note: New arrow images will only appear here after they have been seen in a notification. Check this screen periodically to map any new arrows that are discovered.",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (unassigned.isNotEmpty()) {
                item {
                    Text(text = "Unassigned Arrows", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(unassigned) { mapping ->
                    ArrowMappingCard(mapping, viewModel::onTrendSelected)
                }
            }

            if (assigned.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Assigned Arrows", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(assigned) { mapping ->
                    ArrowMappingCard(mapping, viewModel::onTrendSelected)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArrowMappingCard(
    mapping: ArrowMapping,
    onTrendSelected: (ArrowMapping, CamApsFxState.BloodSugar.Trend) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val trendOptions = CamApsFxState.BloodSugar.Trend.values().toList()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = mapping.bitmap.asImageBitmap(),
                contentDescription = "Arrow Image",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))

            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                TextField(
                    value = mapping.assignedTrend.toHumanReadableString(),
                    onValueChange = {}, // Read-only
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    trendOptions.forEach { trend ->
                        DropdownMenuItem(
                            text = { Text(trend.toHumanReadableString()) },
                            onClick = {
                                onTrendSelected(mapping, trend)
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun CamApsFxState.BloodSugar.Trend.toHumanReadableString(): String {
    return when (this) {
        CamApsFxState.BloodSugar.Trend.RISING_FAST -> "Rising Fast"
        CamApsFxState.BloodSugar.Trend.RISING -> "Rising"
        CamApsFxState.BloodSugar.Trend.RISING_SLOW -> "Rising Slow"
        CamApsFxState.BloodSugar.Trend.STEADY -> "Steady"
        CamApsFxState.BloodSugar.Trend.DROPPING_SLOW -> "Dropping Slow"
        CamApsFxState.BloodSugar.Trend.DROPPING -> "Dropping"
        CamApsFxState.BloodSugar.Trend.DROPPING_FAST -> "Dropping Fast"
        CamApsFxState.BloodSugar.Trend.UNKNOWN -> "Unknown"
        CamApsFxState.BloodSugar.Trend.IGNORE -> "Ignore / Logo"
    }
}
