package com.example.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.RemoteViews
import java.util.ArrayList
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class NotificationListener : NotificationListenerService() {

    private var componentName: ComponentName? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if(componentName == null) {
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
        Log.d("NotificationListener", "onListenerConnected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d("NotificationListener", "onListenerDisconnected")

        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }

        componentName?.let { requestRebind(it) }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        Log.d("NotificationListener", "onNotificationPosted: $sbn")

        val statusBarNotification = sbn?.takeIf { it.packageName == "com.camdiab.fx_alert.mgdl" }
        val notification = statusBarNotification?.notification ?: return
        val contentView = notification.contentView ?: return

        // RemoteViews
        val actionsProperty = RemoteViews::class.memberProperties.first { it.name == "mActions" }
        actionsProperty.isAccessible = true
        val actions = actionsProperty.get(contentView) as ArrayList<*>

        // CamAPS FX
        val action = actions[2]// TODO: Get action for setText with numeric String als value
        val valueProperty = action::class.memberProperties.first { it.name == "value" }
        valueProperty.isAccessible = true
        val value = valueProperty.getter.call(action)

        Log.d("NotificationListener", "mg/dL: $value")
    }

}