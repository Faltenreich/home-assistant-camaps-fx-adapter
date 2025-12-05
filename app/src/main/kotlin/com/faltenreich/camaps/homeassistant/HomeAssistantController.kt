package com.faltenreich.camaps.homeassistant

import android.content.Context
import android.os.Build
import android.util.Log
import com.faltenreich.camaps.BuildConfig
import com.faltenreich.camaps.MainStateProvider
import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.homeassistant.network.HomeAssistantApi
import com.faltenreich.camaps.homeassistant.network.HomeAssistantClient
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody
import com.faltenreich.camaps.settings.SettingsRepository

class HomeAssistantController(context: Context) {

    private val mainStateProvider = MainStateProvider
    private val settingsRepository = SettingsRepository(context)
    private lateinit var homeAssistantClient: HomeAssistantApi

    private val deviceId = "${Build.MANUFACTURER}_${Build.MODEL}".replace(" ", "_")
    private val sensorUniqueId = "${deviceId}_blood_sugar"
    private var webhookId: String? = null

    suspend fun start() {
        val uri = settingsRepository.getHomeAssistantUri()
        val token = settingsRepository.getHomeAssistantToken()
        homeAssistantClient = HomeAssistantClient.getInstance(uri, token)
        
        registerDevice()
    }

    private suspend fun registerDevice() {
        mainStateProvider.addLog("Registering device with Home Assistant")
        val requestBody = HomeAssistantRegisterDeviceRequestBody(
            deviceId = deviceId,
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
        val response = homeAssistantClient.registerDevice(requestBody)
        webhookId = response.webhookId
        mainStateProvider.setHomeAssistantState(HomeAssistantState.ConnectedDevice("Connected with ID: $deviceId"))
        Log.d(TAG, "Device registered: $response")
        registerSensor()
    }

    private suspend fun registerSensor() {
        mainStateProvider.addLog("Registering sensor with Home Assistant")
        val webhookId = webhookId ?: run {
            Log.d(TAG, "Sensor could not be registered due to to missing webhook")
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to register sensor due to missing webhook")
            )
            return
        }
        val requestBody = HomeAssistantRegisterSensorRequestBody(
            data = HomeAssistantRegisterSensorRequestBody.Data(
                uniqueId = sensorUniqueId,
                state = 0f, // Initial state, will be updated shortly
            ),
        )
        try {
            homeAssistantClient.registerSensor(requestBody, webhookId)
            mainStateProvider.setHomeAssistantState(HomeAssistantState.ConnectedSensor("Registered sensor with ID: $sensorUniqueId"))
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
                uniqueId = sensorUniqueId,
                state = data.mmolL,
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