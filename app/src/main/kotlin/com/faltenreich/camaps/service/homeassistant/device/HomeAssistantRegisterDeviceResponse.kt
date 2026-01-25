package com.faltenreich.camaps.service.homeassistant.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantRegisterDeviceResponse(
    @SerialName("cloudhook_url")
    val cloudhookUrl: String?,
    @SerialName("remote_ui_url")
    val remoteUiUrl: String?,
    @SerialName("secret")
    val secret: String?,
    @SerialName("webhook_id")
    val webhookId: String,
)