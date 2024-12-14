package com.faltenreich.camaps.homeassistant.sensor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantUpdateSensorRequestBody(
    @SerialName("type")
    val type: String = "update_sensor_states",
    @SerialName("data")
    val data: Data,
) {

    @Serializable
    data class Data(
        @SerialName("state")
        val state: Float,
    )
}