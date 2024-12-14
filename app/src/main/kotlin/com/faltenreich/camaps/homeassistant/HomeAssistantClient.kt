package com.faltenreich.camaps.homeassistant

import android.util.Log
import com.faltenreich.camaps.BuildConfig
import com.faltenreich.camaps.homeassistant.registration.HomeAssistantRegistrationRequestBody
import com.faltenreich.camaps.homeassistant.registration.HomeAssistantRegistrationResponse
import com.faltenreich.camaps.shared.NetworkClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HomeAssistantClient(
    private val host: String = "http://homeassistant.local:8123",
    private val accessToken: String = BuildConfig.HOME_ASSISTANT_TOKEN,
    private val networkClient: NetworkClient = NetworkClient(
        httpClient = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(
                            accessToken = accessToken,
                            refreshToken = null,
                        )
                    }
                }
            }
        }
    ),
) : HomeAssistantApi {

    override suspend fun register(): HomeAssistantRegistrationResponse {
        val requestBody = HomeAssistantRegistrationRequestBody(
            deviceId = "deviceId",
            appId = "appId",
            appName = "appName",
            appVersion = "appVersion",
            deviceName = "deviceName",
            manufacturer = "manufacturer",
            model = "model",
            osName = "osName",
            osVersion = "osVersion",
            supportsEncryption = false,
        )
        return networkClient.post(
            url = Url("$host/api/mobile_app/registrations"),
            requestBody = requestBody,
        )
    }
}