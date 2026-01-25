package com.faltenreich.camaps.service.homeassistant.network

import com.faltenreich.camaps.service.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.service.homeassistant.device.HomeAssistantRegisterDeviceResponse
import com.faltenreich.camaps.service.homeassistant.sensor.HomeAssistantRegisterBinarySensorRequestBody
import com.faltenreich.camaps.service.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.service.homeassistant.sensor.HomeAssistantRegisterSensorResponse
import com.faltenreich.camaps.service.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody

interface HomeAssistantApi {

    suspend fun ping()

    suspend fun registerDevice(
        requestBody: HomeAssistantRegisterDeviceRequestBody
    ): HomeAssistantRegisterDeviceResponse

    suspend fun registerSensor(
        requestBody: HomeAssistantRegisterSensorRequestBody,
        webhookId: String,
    ): HomeAssistantRegisterSensorResponse

    suspend fun registerBinarySensor(
        requestBody: HomeAssistantRegisterBinarySensorRequestBody,
        webhookId: String,
    ): HomeAssistantRegisterSensorResponse

    suspend fun getSensorState(sensorId: String)

    suspend fun updateSensor(
        requestBody: HomeAssistantUpdateSensorRequestBody,
        webhookId: String,
    )
}