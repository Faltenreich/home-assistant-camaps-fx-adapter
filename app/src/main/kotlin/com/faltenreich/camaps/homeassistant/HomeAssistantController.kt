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
import io.ktor.client.plugins.ResponseException

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
        Log.d(TAG, "start: Starting Home Assistant registration")
        mainStateProvider.addLog("Starting Home Assistant registration")
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
            deviceName = "CamAPS FX Adapter",
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
        mainStateProvider.addLog("Registering sensors")
        listOf("mmol/L", "mg/dL").forEach { unit ->
            registerSensor(unit)
        }
    }

    private suspend fun registerSensor(unit: String) {
        val webhookId = webhookId ?: run {
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to register sensor for $unit due to missing webhook")
            )
            return
        }

        val requestBody = HomeAssistantRegisterSensorRequestBody(
            data = HomeAssistantRegisterSensorRequestBody.Data(
                uniqueId = getSensorUniqueId(unit),
                name = "Blood Sugar ($unit)",
                state = 0f, // Initial state, will be updated shortly
                unitOfMeasurement = unit,
            )
        )

        try {
            val response = homeAssistantClient.registerSensor(requestBody, webhookId)
            mainStateProvider.setHomeAssistantState(HomeAssistantState.ConnectedSensor("Registered sensor for $unit."))
            Log.d(TAG, "Sensor for $unit registered. Response: $response")
        } catch (e: ResponseException) {
            val statusCode = e.response.status.value
            Log.e(TAG, "Sensor for $unit could not be registered. Status: $statusCode", e)
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to register sensor for $unit. Status: $statusCode")
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Sensor for $unit could not be registered: $exception")
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to register sensor for $unit. Message: ${exception.message}")
            )
        }
    }

    suspend fun update(data: CamApsFxState.BloodSugar) {
        val webhookId = webhookId ?: return

        val requestBody = HomeAssistantUpdateSensorRequestBody(
            data = listOf(
                HomeAssistantUpdateSensorRequestBody.Data(
                    uniqueId = getSensorUniqueId(data.unitOfMeasurement),
                    state = data.value,
                    attributes = mapOf("trend" to data.trend?.name)
                )
            )
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
                HomeAssistantState.Error("Failed to update sensor: $exception")
            )
        }
    }

    companion object {

        private val TAG = HomeAssistantController::class.java.simpleName
    }
}