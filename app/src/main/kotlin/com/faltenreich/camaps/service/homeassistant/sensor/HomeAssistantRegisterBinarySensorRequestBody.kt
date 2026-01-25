package com.faltenreich.camaps.service.homeassistant.sensor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantRegisterBinarySensorRequestBody(
    val data: Data,
    val type: String = "register_sensor"
) {
    @Serializable
    data class Data(
        @SerialName("unique_id")
        val uniqueId: String,
        val name: String,
        val state: Boolean,
        val type: String = "binary_sensor",
        @SerialName("device_class")
        val deviceClass: String = "connectivity",
    )
}