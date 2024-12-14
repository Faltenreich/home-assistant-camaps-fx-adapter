package com.faltenreich.camaps.homeassistant

import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceResponse
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody

interface HomeAssistantApi {

    suspend fun register(requestBody: HomeAssistantRegisterDeviceRequestBody): HomeAssistantRegisterDeviceResponse

    suspend fun fireEvent(requestBody: HomeAssistantRegisterSensorRequestBody, webhookId: String): Any
}