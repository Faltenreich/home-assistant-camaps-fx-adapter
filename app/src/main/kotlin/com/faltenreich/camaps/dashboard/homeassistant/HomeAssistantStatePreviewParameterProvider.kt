package com.faltenreich.camaps.dashboard.homeassistant

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.faltenreich.camaps.homeassistant.HomeAssistantState

class HomeAssistantStatePreviewParameterProvider : PreviewParameterProvider<HomeAssistantState> {

    override val values = sequenceOf(
        HomeAssistantState.Disconnected,
        HomeAssistantState.ConnectedDevice,
        HomeAssistantState.ConnectedSensor,
    )
}