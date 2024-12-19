package com.faltenreich.camaps

import android.content.ComponentName
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.faltenreich.camaps.camaps.CamApsFxController
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantController
import com.faltenreich.camaps.homeassistant.HomeAssistantData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            mainStateProvider.event.collectLatest { event ->
                when (event) {
                    // FIXME: Breaks state of service which can then not be started anymore
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
        scope.launch {
            mainStateProvider.state
                .map { it.camApsFx }
                .distinctUntilChanged()
                .collectLatest { state ->
                    when (state) {
                        is CamApsFxState.Blank -> Unit
                        is CamApsFxState.BloodSugar -> {
                            val data = HomeAssistantData.BloodSugar(mgDl = state.mgDl)
                            homeAssistantController.update(data)
                        }
                        is CamApsFxState.Error -> Unit // TODO
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