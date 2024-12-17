package com.faltenreich.camaps.homeassistant

import android.os.Build
import android.util.Log
import com.faltenreich.camaps.BuildConfig
import com.faltenreich.camaps.MainStateProvider
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.homeassistant.network.HomeAssistantClient
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody
import kotlinx.coroutines.flow.collectLatest

class HomeAssistantController {

    private val homeAssistantClient = HomeAssistantClient.Companion.local()
    private val mainStateProvider = MainStateProvider

    private var webhookId: String? = null

    suspend fun start() {
        registerDevice()
        registerSensor()

        mainStateProvider.state.collectLatest { state ->
            update(state.camApsFx)
        }
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
            mainStateProvider.setHomeAssistantState(HomeAssistantState.ConnectedDevice)
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
            homeAssistantClient.registerSensor(requestBody, webhookId)
            mainStateProvider.setHomeAssistantState(HomeAssistantState.ConnectedSensor)
            Log.d(TAG, "Registered sensor")
        } catch (exception: Exception) {
            Log.e(TAG, "Registering sensor failed: $exception")
        }
    }

    private suspend fun update(state: CamApsFxState) {
        val webhookId = webhookId ?: run {
            Log.d(TAG, "Skipping update of sensor due to missing webhook")
            return
        }

        // TODO: Handle other states
        (state as? CamApsFxState.Value) ?: return

        val requestBody = HomeAssistantUpdateSensorRequestBody(
            data = HomeAssistantUpdateSensorRequestBody.Data(
                state = state.bloodSugar.mgDl,
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
            return
        }
        Log.d(TAG, "Updated sensor")
    }

    companion object {

        private val TAG = HomeAssistantController::class.java.simpleName
    }
}