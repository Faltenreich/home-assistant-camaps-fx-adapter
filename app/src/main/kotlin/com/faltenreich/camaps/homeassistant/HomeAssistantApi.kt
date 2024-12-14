package com.faltenreich.camaps.homeassistant

import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceResponse
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody

interface HomeAssistantApi {

    suspend fun registerDevice(requestBody: HomeAssistantRegisterDeviceRequestBody): HomeAssistantRegisterDeviceResponse

    suspend fun registerSensor(requestBody: HomeAssistantRegisterSensorRequestBody, webhookId: String)

    suspend fun updateSensor(requestBody: HomeAssistantUpdateSensorRequestBody, webhookId: String)
}