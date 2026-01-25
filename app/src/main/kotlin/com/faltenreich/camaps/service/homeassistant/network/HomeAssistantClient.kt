package com.faltenreich.camaps.service.homeassistant.network

import com.faltenreich.camaps.core.network.NetworkClient
import com.faltenreich.camaps.service.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.service.homeassistant.device.HomeAssistantRegisterDeviceResponse
import com.faltenreich.camaps.service.homeassistant.sensor.HomeAssistantRegisterBinarySensorRequestBody
import com.faltenreich.camaps.service.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.service.homeassistant.sensor.HomeAssistantRegisterSensorResponse
import com.faltenreich.camaps.service.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody
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

class HomeAssistantClient private constructor(
    private val host: String,
    private val networkClient: NetworkClient,
) : HomeAssistantApi {

    constructor(host: String, token: String) : this(
        host = host,
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

    override suspend fun ping() {
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
}