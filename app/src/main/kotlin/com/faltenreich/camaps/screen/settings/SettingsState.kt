package com.faltenreich.camaps.screen.settings

data class SettingsState(
    val uri: String,
    val token: String,
    val connection: Connection,
) {

    sealed interface Connection {

        data object Idle : Connection

        data object Loading : Connection

        data object Success : Connection

        data class Failure(val message: String) : Connection
    }
}