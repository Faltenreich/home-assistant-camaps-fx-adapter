package com.faltenreich.camaps.homeassistant.registration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantRegistrationResponse(
    @SerialName("cloudhook_url")
    val cloudhookUrl: String?,
    @SerialName("remote_ui_url")
    val remoteUiUrl: String?,
    @SerialName("secret")
    val secret: String?,
    @SerialName("webhook_id")
    val webhookId: String,
)