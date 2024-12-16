package com.faltenreich.camaps.homeassistant

import android.os.Build
import android.util.Log
import com.faltenreich.camaps.BuildConfig
import com.faltenreich.camaps.adapter.BloodSugarEvent
import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HomeAssistantService {

    private val homeAssistantClient = HomeAssistantClient.local()

    private var webhookId: String? = null

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    fun start() = scope.launch {
        registerDevice()
        registerSensor()
    }

    private suspend fun registerDevice() {
        val requestBody = HomeAssistantRegisterDeviceRequestBody(
            deviceId = "deviceId", // TODO: Find unique and consistent identifier?
            appId = BuildConfig.APPLICATION_ID,
            appName = "CamAPS FX Adapter",
            appVersion = BuildConfig.VERSION_NAME,
            deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            osName = "Android",
            osVersion = Build.VERSION.SDK_INT.toString(),
            supportsEncryption = false,
        )
        Log.d(TAG, "Registering device: $requestBody")
        try {
            val response = homeAssistantClient.registerDevice(requestBody)
            webhookId = response.webhookId
            Log.d(TAG, "Registered device: $response")
        } catch (exception: Exception) {
            Log.e(TAG, "Registering device failed: $exception")
        }
    }

    private suspend fun registerSensor() {
        val webhookId = webhookId ?: run {
            Log.d(TAG, "Skipping registration of sensor due to missing webhook")
            return
        }
        val requestBody = HomeAssistantRegisterSensorRequestBody(
            data = HomeAssistantRegisterSensorRequestBody.Data(
                state = 120f,
            ),
        )
        Log.d(TAG, "Registering sensor: $requestBody")
        try {
            homeAssistantClient.registerSensor(
                requestBody = requestBody,
                webhookId = webhookId,
            )
            Log.d(TAG, "Registered sensor")
        } catch (exception: Exception) {
            Log.e(TAG, "Registering sensor failed: $exception")
        }
    }

    fun update(event: BloodSugarEvent) = scope.launch {
        val webhookId = webhookId ?: run {
            Log.d(TAG, "Skipping update of sensor due to missing webhook")
            return@launch
        }
        val requestBody = HomeAssistantUpdateSensorRequestBody(
            data = HomeAssistantUpdateSensorRequestBody.Data(
                state = event.mgDl,
            ),
        )
        Log.d(TAG, "Updating sensor: $requestBody")
        try {
            homeAssistantClient.updateSensor(
                requestBody = requestBody,
                webhookId = webhookId,
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Updating sensor failed: $exception")
            return@launch
        }
        Log.d(TAG, "Updated sensor")
    }

    fun stop() {
        job.cancel()
    }

    companion object {

        private val TAG = HomeAssistantService::class.java.simpleName
    }
}