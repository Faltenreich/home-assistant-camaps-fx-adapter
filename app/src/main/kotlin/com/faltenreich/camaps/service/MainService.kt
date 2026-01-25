package com.faltenreich.camaps.service

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.faltenreich.camaps.MainStateProvider
import com.faltenreich.camaps.ServiceLocator
import com.faltenreich.camaps.service.camaps.CamApsFxController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * NotificationListenerService may break between builds during development.
 * This can be fixed by rebooting the device or toggling notification permissions.
 * https://stackoverflow.com/a/37081128/3269827
 */
class MainService : NotificationListenerService() {

    private val mainStateProvider = MainStateProvider
    private val camApsFxController = CamApsFxController()
    private val homeAssistantController get() = ServiceLocator.homeAssistantController

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        ServiceLocator.setup(this)
        scope.launch {
            mainStateProvider.state
                .map { it.camApsFxState }
                .distinctUntilChanged()
                .collectLatest(homeAssistantController::update)
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
            mainStateProvider.setServiceState(MainServiceState.Connected)
            homeAssistantController.start()
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Service disconnected")
        mainStateProvider.setServiceState(MainServiceState.Disconnected)
    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        Log.d(TAG, "Notification posted")
        camApsFxController.handleNotification(statusBarNotification)
    }

    companion object {

        private val TAG = MainService::class.java.simpleName
    }
}