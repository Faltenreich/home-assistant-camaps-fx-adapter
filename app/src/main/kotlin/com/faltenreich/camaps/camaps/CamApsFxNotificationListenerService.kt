package com.faltenreich.camaps.camaps

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.faltenreich.camaps.adapter.BloodSugarEventAdapter
import com.faltenreich.camaps.homeassistant.HomeAssistantService

class CamApsFxNotificationListenerService : NotificationListenerService() {

    private val notificationMapper = CamApsFxNotificationMapper()
    private val homeAssistantService = HomeAssistantService()
    private val bloodSugarEventAdapter = BloodSugarEventAdapter

    private var componentName: ComponentName? = null

    override fun onCreate() {
        super.onCreate()
        homeAssistantService.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        homeAssistantService.stop()
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
        val event = statusBarNotification?.let(notificationMapper::invoke) ?: return
        homeAssistantService.update(event)
        bloodSugarEventAdapter.postEvent(event)
    }
}