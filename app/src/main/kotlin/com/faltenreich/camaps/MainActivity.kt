package com.faltenreich.camaps

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.faltenreich.camaps.dashboard.Dashboard

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (isNotificationListenerPermissionGranted()) {
            setContent {
                Dashboard()
            }
        } else {
            redirectToSettings()
        }
    }

    private fun isNotificationListenerPermissionGranted(): Boolean {
        val componentName = ComponentName(this, MainService::class.java)
        val enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return enabledListeners?.contains(componentName.flattenToString()) == true
    }

    private fun redirectToSettings() {
        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            startActivityForResult(this, 1001)
        }
    }
}