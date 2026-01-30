package com.faltenreich.camaps.service

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.faltenreich.camaps.AppStateProvider
import com.faltenreich.camaps.ServiceLocator
import com.faltenreich.camaps.locate
import com.faltenreich.camaps.screen.dashboard.log.LogEntryFactory
import com.faltenreich.camaps.service.camaps.CamApsFxController
import com.faltenreich.camaps.service.homeassistant.HomeAssistantController
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

    private val appStateProvider: AppStateProvider get() = locate()
    private val camApsFxController: CamApsFxController get() = locate()
    private val homeAssistantController: HomeAssistantController get() = locate()

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
        Log.d(TAG, "Service destroyed")
        super.onDestroy()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Service connected")
        scope.launch {
            appStateProvider.addLog(LogEntryFactory.create(MainServiceState.Connected))
            homeAssistantController.start()
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Service disconnected")
        appStateProvider.addLog(LogEntryFactory.create(MainServiceState.Disconnected))
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