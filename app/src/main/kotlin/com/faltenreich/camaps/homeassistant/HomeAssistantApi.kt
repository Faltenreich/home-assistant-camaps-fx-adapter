package com.faltenreich.camaps.homeassistant

import android.util.Log
import com.faltenreich.camaps.homeassistant.registration.RegistrationRequestBody
import com.faltenreich.camaps.homeassistant.registration.RegistrationResponse
import io.ktor.http.Url

class HomeAssistantApi(
    private val host: String,
    private val client: NetworkClient,
) {

    suspend fun register() {
        val requestBody = RegistrationRequestBody(
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
        val response = client.post<RegistrationRequestBody, RegistrationResponse>(
            url = Url("$host/api/mobile_app/registrations"),
            requestBody = requestBody,
        )
        Log.d(TAG, "Registered device: $response")
    }

    companion object {

        private val TAG = HomeAssistantApi::class.java.simpleName
    }
}