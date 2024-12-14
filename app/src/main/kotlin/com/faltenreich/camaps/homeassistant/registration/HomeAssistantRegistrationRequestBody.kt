package com.faltenreich.camaps.homeassistant.registration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantRegistrationRequestBody(
    @SerialName("device_id")
    val deviceId: String,
    @SerialName("app_id")
    val appId: String,
    @SerialName("app_name")
    val appName: String,
    @SerialName("app_version")
    val appVersion: String,
    @SerialName("device_name")
    val deviceName: String,
    @SerialName("manufacturer")
    val manufacturer: String,
    @SerialName("model")
    val model: String,
    @SerialName("os_name")
    val osName: String,
    @SerialName("os_version")
    val osVersion: String,
    @SerialName("supports_encryption")
    val supportsEncryption: Boolean,
)