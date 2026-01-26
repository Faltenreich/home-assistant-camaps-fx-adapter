package com.faltenreich.camaps.service.homeassistant

import android.os.Build
import android.util.Log
import com.faltenreich.camaps.BuildConfig
import com.faltenreich.camaps.MainStateProvider
import com.faltenreich.camaps.screen.settings.SettingsRepository
import com.faltenreich.camaps.service.camaps.CamApsFxState
import com.faltenreich.camaps.service.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.service.homeassistant.network.HomeAssistantApi
import com.faltenreich.camaps.service.homeassistant.network.HomeAssistantClient
import com.faltenreich.camaps.service.homeassistant.sensor.HomeAssistantRegisterBinarySensorRequestBody
import com.faltenreich.camaps.service.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.service.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.firstOrNull

class HomeAssistantController(
    private val mainStateProvider: MainStateProvider,
    private val settingsRepository: SettingsRepository,
) {

    private lateinit var homeAssistantClient: HomeAssistantApi

    private var webhookId: String? = null
    private val registeredSensorUniqueIds = mutableSetOf<String>()
    private var isDeviceRegistered = false
    private var lastSentState: CamApsFxState.BloodSugar? = null
    private var lastUpdateTime = 0L
    private val deviceId = settingsRepository.getDeviceId()

    suspend fun start() {
        isDeviceRegistered = false
        Log.d(TAG, "start: Starting Home Assistant registration")
        // FIXME: Vanishes on first start
        val uri = settingsRepository.getHomeAssistantUri().firstOrNull() ?: return
        val token = settingsRepository.getHomeAssistantToken().firstOrNull() ?: return
        homeAssistantClient = HomeAssistantClient(host = uri, token = token)
        webhookId = settingsRepository.getHomeAssistantWebhookId().firstOrNull()
        registeredSensorUniqueIds.addAll(settingsRepository.getRegisteredSensorUniqueIds().firstOrNull() ?: emptySet())

        if (webhookId == null) {
            registerDevice()
        } else {
            validateWebhook()
        }
    }

    private suspend fun validateWebhook() {
        val deviceId = settingsRepository.getDeviceId()
        val dummySensorId = "binary_sensor.camaps_fx_adapter_${deviceId}_dummy_sensor"
        Log.d(TAG, "Validating webhook via dummy sensor: $dummySensorId")

        try {
            homeAssistantClient.getSensorState(dummySensorId)
            isDeviceRegistered = true
            mainStateProvider.setHomeAssistantState(HomeAssistantState.ConnectedDevice("Connected with ID: $deviceId"))
            Log.d(TAG, "Webhook is valid.")
            Log.d(TAG, "Dummy sensor already registered")
            if (registeredSensorUniqueIds.isEmpty()) {
                Log.d(TAG, "Waiting for reading to register sensor..")
            } else {
                Log.d(TAG, "Glucose sensor already registered")
            }
        } catch (e: ResponseException) {
            isDeviceRegistered = false
            val statusCode = e.response.status.value
            Log.w(TAG, "Webhook validation failed with status: $statusCode", e)
            when (statusCode) {
                404, 410 -> {
                    Log.d(TAG, "Webhook is invalid, re-registering device.")
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
            settingsRepository.saveHomeAssistantWebhookId(response.webhookId)
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
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to register dummy sensor", exception)
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

    suspend fun update(state: CamApsFxState) {
        when (state) {
            is CamApsFxState.Blank -> Unit
            is CamApsFxState.BloodSugar -> update(state)
            is CamApsFxState.Error -> Unit // TODO
        }
    }

    private suspend fun update(state: CamApsFxState.BloodSugar) = with(state) {
        if (!isDeviceRegistered) {
            Log.w(TAG, "Device not registered, skipping sensor update for $unitOfMeasurement")
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUpdateTime < 10000) {
            Log.d(TAG, "Update throttled, skipping sensor update")
            return
        }

        if (lastSentState?.value == value) {
            Log.d(TAG, "Sensor state has not changed, skipping update: $value $unitOfMeasurement")
            return
        }

        val webhookId = webhookId ?: run {
            Log.e(TAG, "Device registered but webhookId is null. This should not happen.")
            return
        }

        val sensorUniqueId = getSensorUniqueId(unitOfMeasurement)

        if (sensorUniqueId in registeredSensorUniqueIds) {
            val requestBody = HomeAssistantUpdateSensorRequestBody(
                data = listOf(
                    HomeAssistantUpdateSensorRequestBody.Data(
                        uniqueId = sensorUniqueId,
                        state = value,
                    )
                )
            )

            try {
                homeAssistantClient.updateSensor(
                    requestBody = requestBody,
                    webhookId = webhookId,
                )
                Log.d(TAG, "Sensor updated successfully: $value $unitOfMeasurement")
                lastUpdateTime = currentTime
                lastSentState = state
                mainStateProvider.setHomeAssistantState(HomeAssistantState.UpdatedSensor(HomeAssistantData.BloodSugar(value, unitOfMeasurement)))
            } catch (exception: Exception) {
                Log.e(TAG, "Sensor could not be updated: $exception")
                mainStateProvider.setHomeAssistantState(
                    HomeAssistantState.Error("Failed to update sensor: $exception")
                )
            }
        } else {
            registerSensor(unitOfMeasurement, value)
        }
    }

    private fun getSensorUniqueId(unit: String): String {
        val suffix = unit.replace("/", "_")
        return "${deviceId}_blood_sugar_$suffix".lowercase()
    }

    companion object {

        private val TAG = HomeAssistantController::class.java.simpleName
    }
}
