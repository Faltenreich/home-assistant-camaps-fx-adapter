package com.faltenreich.camaps.homeassistant

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.http.Url

class NetworkClient(@PublishedApi internal val httpClient: HttpClient) {

    suspend inline fun <reified T> request(url: Url): T {
        Log.d(TAG, "Requesting: $url")
        val response = httpClient.request(url)
        Log.d(TAG, "Responding: $response")
        return response.body()
    }

    companion object {

        @PublishedApi internal val TAG = NetworkClient::class.java.simpleName
    }
}