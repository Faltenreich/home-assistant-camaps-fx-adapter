package com.faltenreich.camaps.service.homeassistant.sensor

import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantRegisterSensorResponse(
    val success: Boolean
)
