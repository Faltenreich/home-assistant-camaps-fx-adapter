package com.faltenreich.camaps.camaps

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.faltenreich.camaps.homeassistant.HomeAssistantService

class CamApsFxNotificationListenerService : NotificationListenerService() {

    private val notificationMapper = CamApsFxNotificationMapper()

    private var componentName: ComponentName? = null
    private val homeAssistantService = HomeAssistantService()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        homeAssistantService.start()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        return super.onBind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        homeAssistantService.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStartCommand")

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
        val pm = packageManager
        pm.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        pm.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "onListenerConnected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "onListenerDisconnected")

        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }

        componentName?.let { requestRebind(it) }
    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        Log.d(TAG, "Reading notification")
        val event = statusBarNotification?.let(notificationMapper::invoke) ?: return
        homeAssistantService.update(event)
    }

    companion object {

        private val TAG = CamApsFxNotificationListenerService::class.java.simpleName
    }
}