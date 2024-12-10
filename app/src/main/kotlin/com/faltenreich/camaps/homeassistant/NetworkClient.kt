package com.faltenreich.camaps.homeassistant

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.http.Url

class NetworkClient(@PublishedApi internal val httpClient: HttpClient) {

    suspend inline fun <reified T> request(url: Url): T {
        return httpClient.request(url).body()
    }
}