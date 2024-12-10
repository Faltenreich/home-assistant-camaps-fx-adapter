package com.faltenreich.camaps.homeassistant

import io.ktor.http.Url

class HomeAssistantApi(
    private val host: String,
    private val client: NetworkClient,
) {

    private suspend inline fun <reified T> request(path: String): T {
        return client.request(Url("$host/$path"))
    }

    suspend fun register() {
        request<Any>("api/mobile_app/registrations")
    }
}