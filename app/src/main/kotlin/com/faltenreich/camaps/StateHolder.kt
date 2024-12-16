package com.faltenreich.camaps

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantState
import kotlinx.coroutines.flow.MutableStateFlow

object StateHolder {

    val camApsFxState = MutableStateFlow<CamApsFxState>(CamApsFxState.None)
    val homeAssistantState = MutableStateFlow<HomeAssistantState>(HomeAssistantState.Disconnected)
}