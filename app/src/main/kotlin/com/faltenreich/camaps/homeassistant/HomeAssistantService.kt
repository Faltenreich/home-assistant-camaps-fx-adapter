package com.faltenreich.camaps.homeassistant

import android.os.Build
import android.util.Log
import com.faltenreich.camaps.BloodSugarEvent
import com.faltenreich.camaps.BuildConfig
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
        val registerDeviceRequestBody = HomeAssistantRegisterDeviceRequestBody(
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
        Log.d(TAG, "Registering device: $registerDeviceRequestBody")
        val registerDeviceResponse = try {
            homeAssistantClient.registerDevice(registerDeviceRequestBody)
        } catch (exception: Exception) {
            Log.e(TAG, "Registering device failed: $exception")
            return@launch
        }
        Log.d(TAG, "Registered device: $registerDeviceResponse")

        // FIXME: HTTP 200 but no sensor is visible in home assistant
        val registerSensorRequestBody = HomeAssistantRegisterSensorRequestBody(
            data = HomeAssistantRegisterSensorRequestBody.Data(
                state = 120f,
            ),
        )
        Log.d(TAG, "Registering sensor: $registerSensorRequestBody")
        val registerSensorResponse = try {
            homeAssistantClient.registerSensor(
                requestBody = registerSensorRequestBody,
                webhookId = registerDeviceResponse.webhookId,
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Registering sensor failed: $exception")
            return@launch
        }
        Log.d(TAG, "Registered sensor: $registerSensorResponse")

        webhookId = registerDeviceResponse.webhookId
    }

    fun update(event: BloodSugarEvent) = scope.launch {
        val webhookId = webhookId ?: run {
            Log.d(TAG, "Skipping update due to missing webhook")
            return@launch
        }
        val updateSensorRequestBody = HomeAssistantUpdateSensorRequestBody(
            data = HomeAssistantUpdateSensorRequestBody.Data(
                state = event.mgDl,
            ),
        )
        Log.d(TAG, "Updating sensor: $updateSensorRequestBody")
        val updateSensorResponse = try {
            homeAssistantClient.updateSensor(
                requestBody = updateSensorRequestBody,
                webhookId = webhookId,
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Updating sensor failed: $exception")
            return@launch
        }
        Log.d(TAG, "Updated sensor: $updateSensorResponse")
    }

    fun stop() {
        job.cancel()
    }

    companion object {

        private val TAG = HomeAssistantService::class.java.simpleName
    }
}