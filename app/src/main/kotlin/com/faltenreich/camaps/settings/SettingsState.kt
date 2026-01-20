package com.faltenreich.camaps.settings

data class SettingsState(
    val uri: String,
    val token: String,
    val notificationTimeoutMinutes: Int,
    val connection: Connection,
    val hasPermission: Boolean,
) {

    sealed interface Connection {

        object Idle : Connection

        object Loading : Connection

        object Success : Connection

        data class Failure(val message: String) : Connection
    }
}