package com.faltenreich.camaps.homeassistant

import android.os.Build
import android.util.Log
import com.faltenreich.camaps.BuildConfig
import com.faltenreich.camaps.MainStateProvider
import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.homeassistant.network.HomeAssistantClient
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody

class HomeAssistantController {

    private val mainStateProvider = MainStateProvider
    private val homeAssistantClient = HomeAssistantClient.Companion.local()

    private var webhookId: String? = null

    suspend fun start() {
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
        try {
            val response = homeAssistantClient.registerDevice(requestBody)
            webhookId = response.webhookId
            mainStateProvider.setHomeAssistantState(HomeAssistantState.ConnectedDevice)
            Log.d(TAG, "Device registered: $response")
        } catch (exception: Exception) {
            Log.e(TAG, "Device could not be registered: $exception")
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to register device due to $exception")
            )
        }
    }

    private suspend fun registerSensor() {
        val webhookId = webhookId ?: run {
            Log.d(TAG, "Sensor could not be registered due to to missing webhook")
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to register sensor due to missing webhook")
            )
            return
        }
        val requestBody = HomeAssistantRegisterSensorRequestBody(
            data = HomeAssistantRegisterSensorRequestBody.Data(
                state = 120f,
            ),
        )
        try {
            homeAssistantClient.registerSensor(requestBody, webhookId)
            mainStateProvider.setHomeAssistantState(HomeAssistantState.ConnectedSensor)
            Log.d(TAG, "Sensor registered")
        } catch (exception: Exception) {
            Log.e(TAG, "Sensor could not be registered: $exception")
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to register sensor due to $exception")
            )
        }
    }

    suspend fun update(data: HomeAssistantData) {
        val webhookId = webhookId ?: run {
            Log.d(TAG, "Sensor could not be updated due to to missing webhook")
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to update sensor due to missing webhook")
            )
            return
        }

        // TODO: Handle other states
        (data as? HomeAssistantData.BloodSugar) ?: return

        val requestBody = HomeAssistantUpdateSensorRequestBody(
            data = HomeAssistantUpdateSensorRequestBody.Data(
                state = data.mgDl,
            ),
        )
        try {
            homeAssistantClient.updateSensor(
                requestBody = requestBody,
                webhookId = webhookId,
            )
            Log.d(TAG, "Sensor updated")
            mainStateProvider.setHomeAssistantState(HomeAssistantState.UpdatedSensor(data))
        } catch (exception: Exception) {
            Log.e(TAG, "Sensor could not be updated: $exception")
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to update sensor due to $exception")
            )
        }
    }

    companion object {

        private val TAG = HomeAssistantController::class.java.simpleName
    }
}