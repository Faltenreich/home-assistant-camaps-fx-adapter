package com.faltenreich.camaps.homeassistant.webhook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: Make more generic?
@Serializable
data class HomeAssistantWebhookRequestBody(
    @SerialName("type")
    val type: String,
    @SerialName("data")
    val data: FireEventData,
) {

    @Serializable
    data class FireEventData(
        @SerialName("event_type")
        val eventType: String,
        @SerialName("event_data")
        val eventData: BloodSugar,
    ) {

        @Serializable
        data class BloodSugar(
            @SerialName("value")
            val value: String,
        )
    }

    companion object {

        fun bloodSugar(value: String): HomeAssistantWebhookRequestBody {
            return HomeAssistantWebhookRequestBody(
                type = "fire_event",
                data = FireEventData(
                    eventType = "blood_sugar",
                    eventData = FireEventData.BloodSugar(
                        value = value,
                    ),
                ),
            )
        }
    }
}