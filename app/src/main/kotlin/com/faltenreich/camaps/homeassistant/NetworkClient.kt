package com.faltenreich.camaps.homeassistant

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType

class NetworkClient(@PublishedApi internal val httpClient: HttpClient) {


    suspend inline fun <reified Response> get(url: Url): Response {
        Log.d(TAG, "Requesting: GET $url")
        val response = httpClient.get(url)
        Log.d(TAG, "Responding: GET $response")
        return response.body()
    }

    suspend inline fun <reified RequestBody, reified Response> post(url: Url, requestBody: RequestBody): Response {
        Log.d(TAG, "Requesting: POST $url")
        val response = httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }
        Log.d(TAG, "Responding: POST $response")
        return response.body()
    }

    companion object {

        @PublishedApi internal val TAG = NetworkClient::class.java.simpleName
    }
}