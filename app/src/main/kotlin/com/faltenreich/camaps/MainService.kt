package com.faltenreich.camaps

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.faltenreich.camaps.camaps.CamApsFxController
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantController
import com.faltenreich.camaps.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

/**
 * NotificationListenerService breaks between builds during development.
 * This can be fixed by rebooting the device or toggling notification permission.
 * see https://stackoverflow.com/a/37081128/3269827
 */
class MainService : NotificationListenerService() {

    private val mainStateProvider = MainStateProvider
    private val camApsFxController = CamApsFxController()
    private lateinit var homeAssistantController: HomeAssistantController
    private val settingsRepository: SettingsRepository = SettingsRepository
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: Service creating")
        mainStateProvider.addLog("Service creating")
        homeAssistantController = HomeAssistantController(settingsRepository)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind: Service binding")
        mainStateProvider.addLog("Service binding")
        return super.onBind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Service destroying")
        mainStateProvider.addLog("Service destroying")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "onListenerConnected: Service connected")
        mainStateProvider.addLog("Service connected")

        scope.launch {
            mainStateProvider.setServiceState(MainServiceState.Connected)
            try {
                homeAssistantController.start()
            } catch (_: Exception) {
                mainStateProvider.addLog("Failed to connect to Home Assistant. Retrying in 10 minutes.")
                delay(10.minutes)
                homeAssistantController.start()
            }
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "onListenerDisconnected: Service disconnected")
        mainStateProvider.addLog("Service disconnected. If this was unexpected, try toggling the notification permission.")
        mainStateProvider.setServiceState(MainServiceState.Disconnected)
    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        Log.d(TAG, "onNotificationPosted: $statusBarNotification")
        val state = camApsFxController.handleNotification(this, statusBarNotification)
        if (state is CamApsFxState.BloodSugar) {
            scope.launch {
                homeAssistantController.update(state)
            }
        }
    }

    companion object {

        private val TAG = MainService::class.java.simpleName
    }
}
