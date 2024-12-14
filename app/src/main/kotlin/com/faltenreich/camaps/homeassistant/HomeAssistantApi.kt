package com.faltenreich.camaps.homeassistant

import com.faltenreich.camaps.homeassistant.registration.HomeAssistantRegistrationRequestBody
import com.faltenreich.camaps.homeassistant.registration.HomeAssistantRegistrationResponse

interface HomeAssistantApi {

    suspend fun register(requestBody: HomeAssistantRegistrationRequestBody): HomeAssistantRegistrationResponse
}