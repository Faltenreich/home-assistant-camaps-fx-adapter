package com.faltenreich.camaps

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.faltenreich.camaps.camaps.CamApsFxController
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantController
import com.faltenreich.camaps.homeassistant.HomeAssistantData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * NotificationListenerService breaks between builds during development.
 * This can be fixed by rebooting the device or toggling notification permission.
 * see https://stackoverflow.com/a/37081128/3269827
 */
class MainService : NotificationListenerService() {

    private val mainStateProvider = MainStateProvider
    private val camApsFxController = CamApsFxController()
    private val homeAssistantController = HomeAssistantController()

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        scope.launch {
            mainStateProvider.state
                .map { it.camApsFxState }
                .distinctUntilChanged()
                .collectLatest { state ->
                    when (state) {
                        is CamApsFxState.Blank -> Unit
                        is CamApsFxState.Off -> Unit // TODO
                        is CamApsFxState.Starting -> Unit // TODO
                        is CamApsFxState.BloodSugar -> {
                            val data = HomeAssistantData.BloodSugar(mgDl = state.mgDl)
                            homeAssistantController.update(data)
                        }
                        is CamApsFxState.Error -> Unit // TODO
                    }
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