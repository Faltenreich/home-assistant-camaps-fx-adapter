package com.faltenreich.camaps.homeassistant

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.request
import io.ktor.http.Url

class Networking {

    private val client = HttpClient(OkHttp)

    suspend fun request(url: Url) {
        client.request(url)
    }
}