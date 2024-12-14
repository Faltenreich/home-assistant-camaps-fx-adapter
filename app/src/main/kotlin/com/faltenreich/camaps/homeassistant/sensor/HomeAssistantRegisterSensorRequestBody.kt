package com.faltenreich.camaps.homeassistant.sensor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantRegisterSensorRequestBody(
    @SerialName("type")
    val type: String,
    @SerialName("data")
    val data: Data,
) {

    @Serializable
    data class Data(
        @SerialName("event_type")
        val eventType: String,
        @SerialName("event_data")
        val eventData: EventData,
    ) {

        @Serializable
        data class EventData(
            @SerialName("value")
            val value: String,
        )
    }
}