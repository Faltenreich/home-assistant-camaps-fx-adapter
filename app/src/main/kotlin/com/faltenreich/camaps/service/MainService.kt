package com.faltenreich.camaps.service

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.faltenreich.camaps.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * NotificationListenerService may break between builds during development.
 * This can be fixed by rebooting the device or toggling notification permissions.
 * https://stackoverflow.com/a/37081128/3269827
 */
class MainService : NotificationListenerService() {

    private val appStateProvider get() = ServiceLocator.appStateProvider
    private val camApsFxController get() = ServiceLocator.camApsFxController
    private val homeAssistantController get() = ServiceLocator.homeAssistantController

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        ServiceLocator.setup(this)

        scope.launch {
            appStateProvider.camApsFxEvent.collectLatest { event ->
                homeAssistantController.update(event)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "Service bound")
        return super.onBind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Service connected")
        scope.launch {
            appStateProvider.setServiceState(MainServiceState.Connected)
            homeAssistantController.start()
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Service disconnected")
        appStateProvider.setServiceState(MainServiceState.Disconnected)
    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        Log.d(TAG, "Notification posted")
        scope.launch {
            camApsFxController.handleNotification(statusBarNotification)
        }
    }

    companion object {

        private val TAG = MainService::class.java.simpleName
    }
}