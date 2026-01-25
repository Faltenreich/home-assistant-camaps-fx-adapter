package com.faltenreich.camaps.service.homeassistant.sensor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantRegisterSensorRequestBody(
    @SerialName("type")
    val type: String = "register_sensor",
    @SerialName("data")
    val data: Data,
) {

    @Serializable
    data class Data(
        @SerialName("device_class")
        val deviceClass: String = "blood_glucose_concentration",
        @SerialName("icon")
        val icon: String = "mdi:water-alert",
        @SerialName("name")
        val name: String,
        @SerialName("state")
        val state: Float,
        @SerialName("type")
        val type: String = "sensor",
        @SerialName("unique_id")
        val uniqueId: String,
        @SerialName("unit_of_measurement")
        val unitOfMeasurement: String,
        @SerialName("state_class")
        val stateClass: String = "measurement",
        @SerialName("entity_category")
        val entityCategory: String = "diagnostic",
        @SerialName("disabled")
        val disabled: Boolean = false,
    )
}