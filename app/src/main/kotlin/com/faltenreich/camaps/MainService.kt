package com.faltenreich.camaps

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.faltenreich.camaps.camaps.CamApsFxController
import com.faltenreich.camaps.homeassistant.HomeAssistantController

class MainService : NotificationListenerService() {

    private val camApsFxController = CamApsFxController()
    private val homeAssistantController = HomeAssistantController()

    private var componentName: ComponentName? = null

    override fun onCreate() {
        super.onCreate()
        homeAssistantController.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        homeAssistantController.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }
        componentName?.let {
            requestRebind(it)
            toggleNotificationListenerService(it)
        }
        return START_REDELIVER_INTENT
    }

    private fun toggleNotificationListenerService(componentName: ComponentName) {
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP,
        )
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP,
        )
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }
        componentName?.let { requestRebind(it) }
    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        camApsFxController.handleNotification(statusBarNotification)
    }
}