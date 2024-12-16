package com.faltenreich.camaps

import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantState

data class MainState(
    val camApsFxState: CamApsFxState,
    val homeAssistantState: HomeAssistantState,
) {

    companion object {

        val Initial = MainState(
            camApsFxState = CamApsFxState.None,
            homeAssistantState = HomeAssistantState.Disconnected,
        )
    }
}