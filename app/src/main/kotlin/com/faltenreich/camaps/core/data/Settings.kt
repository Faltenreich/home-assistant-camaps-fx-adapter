package com.faltenreich.camaps.core.data

data class Settings(
    val homeAssistant: HomeAssistant?,
) {

    data class HomeAssistant(
        val uri: String,
        val token: String,
        val webhookId: String?,
        val registeredSensorUniqueIds: Set<String>,
    )
}