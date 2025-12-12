package com.faltenreich.camaps.homeassistant

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.faltenreich.camaps.BuildConfig
import com.faltenreich.camaps.MainStateProvider
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.homeassistant.network.HomeAssistantApi
import com.faltenreich.camaps.homeassistant.network.HomeAssistantClient
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterBinarySensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody
import com.faltenreich.camaps.settings.SettingsRepository
import io.ktor.client.plugins.ResponseException

class HomeAssistantController(context: Context) {

    private val mainStateProvider = MainStateProvider
    private val settingsRepository = SettingsRepository(context)
    private lateinit var homeAssistantClient: HomeAssistantApi
    private val deviceId: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    private var webhookId: String? = null
    private val registeredSensorUniqueIds = mutableSetOf<String>()
    private var isDeviceRegistered = false
    private var lastSentState: CamApsFxState.BloodSugar? = null

    private val dummySensorId: String
        get() = "binary_sensor.camaps_fx_adapter_${deviceId}_dummy_sensor"

    private fun getSensorUniqueId(unit: String): String {
        val suffix = unit.replace("/", "_")
        return "${deviceId}_blood_sugar_$suffix".lowercase()
    }

    suspend fun start() {
        isDeviceRegistered = false
        Log.d(TAG, "start: Starting Home Assistant registration")
        mainStateProvider.addLog("Starting Home Assistant registration")
        val uri = settingsRepository.getHomeAssistantUri()
        val token = settingsRepository.getHomeAssistantToken()
        homeAssistantClient = HomeAssistantClient.getInstance(uri, token)
        webhookId = settingsRepository.getHomeAssistantWebhookId().takeIf { it.isNotBlank() }
        registeredSensorUniqueIds.addAll(settingsRepository.getRegisteredSensorUniqueIds())

        if (webhookId == null) {
            registerDevice()
        } else {
            validateWebhook()
        }
    }

    private suspend fun validateWebhook() {
        mainStateProvider.addLog("Validating existing webhook")
        Log.d(TAG, "Validating webhook via dummy sensor: $dummySensorId")

        try {
            homeAssistantClient.getSensorState(dummySensorId)
            isDeviceRegistered = true
            mainStateProvider.setHomeAssistantState(HomeAssistantState.ConnectedDevice("Connected with ID: $deviceId"))
            Log.d(TAG, "Webhook is valid.")
            mainStateProvider.addLog("Dummy sensor already registered")
            if (registeredSensorUniqueIds.isEmpty()) {
                mainStateProvider.addLog("Waiting for reading to register sensor..")
            } else {
                mainStateProvider.addLog("Glucose sensor already registered")
            }
        } catch (e: ResponseException) {
            isDeviceRegistered = false
            val statusCode = e.response.status.value
            Log.w(TAG, "Webhook validation failed with status: $statusCode", e)
            when (statusCode) {
                404, 410 -> {
                    mainStateProvider.addLog("Webhook is invalid, re-registering device.")
                    registerDevice()
                }
                401, 403 -> {
                    mainStateProvider.setHomeAssistantState(
                        HomeAssistantState.Error("Authentication error. Please check your Home Assistant token.")
                    )
                }
                else -> {
                    mainStateProvider.setHomeAssistantState(
                        HomeAssistantState.Error("Failed to validate webhook. Status: $statusCode")
                    )
                }
            }
        } catch (exception: Exception) {
            isDeviceRegistered = false
            Log.e(TAG, "Webhook validation failed", exception)
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to validate webhook: ${exception.message}")
            )
        }
    }

    private suspend fun registerDevice() {
        isDeviceRegistered = false
        mainStateProvider.addLog("Registering device with Home Assistant")
        Log.d(TAG, "Device ID: $deviceId")
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
            appData = emptyMap(),
            identifiers = listOf("device_$deviceId"),
        )
        try {
            val response = homeAssistantClient.registerDevice(requestBody)
            webhookId = response.webhookId
            response.webhookId?.let { settingsRepository.saveHomeAssistantWebhookId(it) }
            registeredSensorUniqueIds.clear()
            settingsRepository.clearRegisteredSensorUniqueIds()
            isDeviceRegistered = true
            mainStateProvider.setHomeAssistantState(HomeAssistantState.ConnectedDevice("Connected with ID: $deviceId"))
            Log.d(TAG, "Device registered: $response")
        } catch (exception: Exception) {
            Log.e(TAG, "Device could not be registered: $exception")
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to register device: ${exception.message}")
            )
        }
        if (isDeviceRegistered) {
            registerDummySensor()
        }
    }

    private suspend fun registerDummySensor() {
        val webhookId = webhookId ?: return
        mainStateProvider.addLog("Registering dummy sensor")
        val uniqueId = "${deviceId}_dummy_sensor"
        try {
            val requestBody = HomeAssistantRegisterBinarySensorRequestBody(
                data = HomeAssistantRegisterBinarySensorRequestBody.Data(
                    uniqueId = uniqueId,
                    name = uniqueId,
                    state = true,
                )
            )
            homeAssistantClient.registerBinarySensor(requestBody, webhookId)
            mainStateProvider.addLog("Registered dummy sensor")
        } catch (exception: Exception) {
            mainStateProvider.addLog("Failed to register dummy sensor..")
        }
    }

    private suspend fun registerSensor(unit: String, state: Float) {
        val webhookId = webhookId ?: run {
            mainStateProvider.setHomeAssistantState(
                HomeAssistantState.Error("Failed to register sensor for $unit due to missing webhook")
            )
            return
        }

        val uniqueId = getSensorUniqueId(unit)
        val requestBody = HomeAssistantRegisterSensorRequestBody(
            data = HomeAssistantRegisterSensorRequestBody.Data(
                uniqueId = uniqueId,
                name = "Blood Sugar ($unit)",
                state = state,
                unitOfMeasurement = unit,
            )
        )

        try {
            val response = homeAssistantClient.registerSensor(requestBody, webhookId)
            registeredSensorUniqueIds.add(uniqueId)
            settingsRepository.saveRegisteredSensorUniqueIds(registeredSensorUniqueIds)
            mainStateProvider.setHomeAssistantState(HomeAssistantState.ConnectedSensor("Registered sensor: $state $unit."))
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
        if (!isDeviceRegistered) {
            Log.w(TAG, "Device not registered, skipping sensor update for ${data.unitOfMeasurement}")
            return
        }

        if (lastSentState?.value == data.value && lastSentState?.trend == data.trend) {
            Log.d(TAG, "Sensor state has not changed, skipping update: ${data.value} ${data.unitOfMeasurement}, Trend: ${data.trend}")
            return
        }

        val webhookId = webhookId ?: run {
            Log.e(TAG, "Device registered but webhookId is null. This should not happen.")
            return
        }

        val sensorUniqueId = getSensorUniqueId(data.unitOfMeasurement)

        if (sensorUniqueId in registeredSensorUniqueIds) {
            val requestBody = HomeAssistantUpdateSensorRequestBody(
                data = listOf(
                    HomeAssistantUpdateSensorRequestBody.Data(
                        uniqueId = sensorUniqueId,
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
                Log.d(TAG, "Sensor updated successfully: ${data.value} ${data.unitOfMeasurement}")
                lastSentState = data
                mainStateProvider.setHomeAssistantState(HomeAssistantState.UpdatedSensor(HomeAssistantData.BloodSugar(data.value, data.unitOfMeasurement, data.trend)))
            } catch (exception: Exception) {
                Log.e(TAG, "Sensor could not be updated: $exception")
                mainStateProvider.setHomeAssistantState(
                    HomeAssistantState.Error("Failed to update sensor: $exception")
                )
            }
        } else {
            registerSensor(data.unitOfMeasurement, data.value)
        }
    }

    companion object {

        private val TAG = HomeAssistantController::class.java.simpleName
    }
}
