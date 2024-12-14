package com.faltenreich.camaps.homeassistant

import com.faltenreich.camaps.homeassistant.registration.HomeAssistantRegistrationRequestBody
import com.faltenreich.camaps.homeassistant.registration.HomeAssistantRegistrationResponse
import com.faltenreich.camaps.homeassistant.webhook.HomeAssistantWebhookRequestBody

interface HomeAssistantApi {

    suspend fun register(requestBody: HomeAssistantRegistrationRequestBody): HomeAssistantRegistrationResponse

    suspend fun fireEvent(requestBody: HomeAssistantWebhookRequestBody, webhookId: String): Any
}