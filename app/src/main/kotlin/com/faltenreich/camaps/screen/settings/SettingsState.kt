package com.faltenreich.camaps.screen.settings

data class SettingsState(
    val uri: String,
    val token: String,
    val connection: Connection,
) {

    sealed interface Connection {

        object Loading : Connection

        object Success : Connection

        data class Failure(val message: String) : Connection
    }
}