package com.faltenreich.camaps.homeassistant.sensor

import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantRegisterSensorResponse(
    val success: Boolean
)
