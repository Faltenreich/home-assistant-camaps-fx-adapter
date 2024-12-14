package com.faltenreich.camaps.homeassistant

import io.ktor.http.Url

class HomeAssistantApi(
    private val host: String,
    private val client: NetworkClient,
) {

    suspend fun register() {
        val body = RegistrationBody(
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
        client.post<RegistrationBody, Any>(
            url = Url("$host/api/mobile_app/registrations"),
            body = body,
        )
    }
}