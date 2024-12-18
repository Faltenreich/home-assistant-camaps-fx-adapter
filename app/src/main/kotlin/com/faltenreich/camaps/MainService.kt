package com.faltenreich.camaps

import android.content.ComponentName
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.faltenreich.camaps.camaps.CamApsFxController
import com.faltenreich.camaps.homeassistant.HomeAssistantController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainService : NotificationListenerService() {

    private val mainStateProvider = MainStateProvider
    private val camApsFxController = CamApsFxController()
    private val homeAssistantController = HomeAssistantController()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            mainStateProvider.event.collectLatest { event ->
                when (event) {
                    is MainEvent.ToggleService -> when (mainStateProvider.state.value.service) {
                        is MainServiceState.Disconnected -> {
                            val service = this@MainService
                            val componentName = ComponentName(service, service::class.java)
                            requestRebind(componentName)
                        }
                        is MainServiceState.Connected -> requestUnbind()
                    }
                }
            }
        }
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
        job.cancel()
    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        camApsFxController.handleNotification(statusBarNotification)
    }

    companion object {

        private val TAG = MainService::class.java.simpleName
    }
}