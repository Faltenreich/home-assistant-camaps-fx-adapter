package com.faltenreich.camaps

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.faltenreich.camaps.camaps.CamApsFxController
import com.faltenreich.camaps.homeassistant.HomeAssistantController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainService : NotificationListenerService() {

    private val camApsFxController = CamApsFxController()
    private val homeAssistantController = HomeAssistantController()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onListenerConnected() {
        super.onListenerConnected()
        scope.launch {
            homeAssistantController.start()
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        job.cancel()
    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        camApsFxController.handleNotification(statusBarNotification)
    }
}