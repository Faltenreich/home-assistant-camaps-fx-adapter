package com.faltenreich.camaps.homeassistant.network

import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceResponse
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterBinarySensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorResponse
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HomeAssistantClient(
    private val host: String,
    private val token: String,
    private val networkClient: NetworkClient,
) : HomeAssistantApi {

    override suspend fun testConnection() {
        networkClient.get<Unit>(url = Url("$host/api/"))
    }

    override suspend fun registerDevice(
        requestBody: HomeAssistantRegisterDeviceRequestBody,
    ): HomeAssistantRegisterDeviceResponse {
        return networkClient.post(
            url = Url("$host/api/mobile_app/registrations"),
            requestBody = requestBody,
        )
    }

    override suspend fun registerSensor(
        requestBody: HomeAssistantRegisterSensorRequestBody,
        webhookId: String,
    ): HomeAssistantRegisterSensorResponse {
        return networkClient.post(
            url = Url("$host/api/webhook/$webhookId"),
            requestBody = requestBody,
        )
    }

    override suspend fun registerBinarySensor(
        requestBody: HomeAssistantRegisterBinarySensorRequestBody,
        webhookId: String,
    ): HomeAssistantRegisterSensorResponse {
        return networkClient.post(
            url = Url("$host/api/webhook/$webhookId"),
            requestBody = requestBody,
        )
    }

    override suspend fun getSensorState(sensorId: String) {
        return networkClient.get(
            url = Url("$host/api/states/$sensorId"),
        )
    }

    override suspend fun updateSensor(
        requestBody: HomeAssistantUpdateSensorRequestBody,
        webhookId: String,
    ) {
        return networkClient.post(
            url = Url("$host/api/webhook/$webhookId"),
            requestBody = requestBody,
        )
    }

    companion object {
        fun getInstance(host: String, token: String): HomeAssistantApi {
            return HomeAssistantClient(
                host = host,
                token = token,
                networkClient = NetworkClient(
                    httpClient = HttpClient(OkHttp) {
                        expectSuccess = true
                        defaultRequest {
                            headers.append("User-Agent", "CamAPS-FX-Adapter")
                        }
                        install(ContentNegotiation) {
                            json(
                                Json {
                                    encodeDefaults = true
                                    prettyPrint = true
                                    isLenient = true
                                    ignoreUnknownKeys = true
                                }
                            )
                        }
                        install(Auth) {
                            bearer {
                                loadTokens {
                                    BearerTokens(
                                        accessToken = token.trim(),
                                        refreshToken = null,
                                    )
                                }
                            }
                        }
                    }
                ),
            )
        }
    }
}