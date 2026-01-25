package com.faltenreich.camaps.service.homeassistant.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantRegisterDeviceRequestBody(
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
    @SerialName("app_data")
    val appData: Map<String, String?>,
    @SerialName("identifiers")
    val identifiers: List<String>,
)