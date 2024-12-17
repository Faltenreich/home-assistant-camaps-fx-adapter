package com.faltenreich.camaps.homeassistant.network

import com.faltenreich.camaps.BuildConfig
import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceResponse
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.String

class HomeAssistantClient(
    private val host: String,
    private val networkClient: NetworkClient,
) : HomeAssistantApi {

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
    ) {
        return networkClient.post(
            url = Url("$host/api/webhook/$webhookId"),
            requestBody = requestBody,
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

        fun local(): HomeAssistantApi {
            return HomeAssistantClient(
                host = "http://homeassistant.local:8123",
                networkClient = NetworkClient(
                    httpClient = HttpClient(OkHttp) {
                        expectSuccess = true
                        install(ContentNegotiation) {
                            json(
                                Json {
                                    encodeDefaults = true
                                    prettyPrint = true
                                    isLenient = true
                                }
                            )
                        }
                        install(Auth) {
                            bearer {
                                loadTokens {
                                    BearerTokens(
                                        accessToken = BuildConfig.HOME_ASSISTANT_TOKEN,
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