package com.faltenreich.camaps.homeassistant

import android.content.Context
import android.os.Build
import android.util.Log
import com.faltenreich.camaps.BuildConfig
import com.faltenreich.camaps.MainStateProvider
import com.faltenreich.camaps.camaps.CamApsFxState
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
    private var webhookId: String? = null

    private fun getSensorUniqueId(unit: String): String {
        val suffix = if (unit.startsWith("mmol")) "mmol" else "mg"
        return "${deviceId}_blood_sugar_$suffix"
    }

    suspend fun start() {
        Log.d(TAG, "start: Kicking off Home Assistant registration")
        mainStateProvider.addLog("Kicking off Home Assistant registration")
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
        registerSensors()
    }

    private suspend fun registerSensors() {
        listOf("mmol/L", "mg/dL").forEach { unit ->
            registerSensor(unit)
        }
    }

    private suspend fun registerSensor(unit: String) {
        mainStateProvider.addLog("Registering sensor for $unit")
        val webhookId = webhookId ?: run {
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to register sensor for $unit due to missing webhook")
            )
            return
        }
        val requestBody = HomeAssistantRegisterSensorRequestBody(
            data = HomeAssistantRegisterSensorRequestBody.Data(
                uniqueId = getSensorUniqueId(unit),
                state = 0f, // Initial state, will be updated shortly
                unitOfMeasurement = unit,
            ),
        )
        try {
            homeAssistantClient.registerSensor(requestBody, webhookId)
            mainStateProvider.setHomeAssistantState(HomeAssistantState.ConnectedSensor("Registered sensor with ID: ${getSensorUniqueId(unit)}"))
            Log.d(TAG, "Sensor for $unit registered")
        } catch (exception: Exception) {
            Log.e(TAG, "Sensor for $unit could not be registered: $exception")
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to register sensor for $unit due to $exception")
            )
        }
    }

    suspend fun update(data: CamApsFxState.BloodSugar) {
        val webhookId = webhookId ?: return

        val requestBody = HomeAssistantUpdateSensorRequestBody(
            data = HomeAssistantUpdateSensorRequestBody.Data(
                uniqueId = getSensorUniqueId(data.unitOfMeasurement),
                state = data.value,
                attributes = mapOf("trend" to data.trend?.name)
            ),
        )
        try {
            homeAssistantClient.updateSensor(
                requestBody = requestBody,
                webhookId = webhookId,
            )
            Log.d(TAG, "Sensor updated")
            mainStateProvider.setHomeAssistantState(HomeAssistantState.UpdatedSensor(HomeAssistantData.BloodSugar(data.value, data.unitOfMeasurement)))
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