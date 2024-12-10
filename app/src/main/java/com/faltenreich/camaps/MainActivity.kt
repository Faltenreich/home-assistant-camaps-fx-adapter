package com.faltenreich.camaps

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (!isNotificationListenerPermissionGranted()) {
            redirectToSettings()
        }
    }

    fun redirectToSettings() {
        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            startActivityForResult(this, 1001)
        }
    }

    fun isNotificationListenerPermissionGranted(): Boolean {
        val componentName = ComponentName(this, NotificationListener::class.java)
        val enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return enabledListeners?.contains(componentName.flattenToString()) ?: false
    }
}