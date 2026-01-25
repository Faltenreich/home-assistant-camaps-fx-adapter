package com.faltenreich.camaps.homeassistant.network

import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceResponse
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterBinarySensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorResponse
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody

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