package com.faltenreich.camaps

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.faltenreich.camaps.camaps.CamApsFxController
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.homeassistant.HomeAssistantController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainService : NotificationListenerService() {

    private val mainStateProvider = MainStateProvider
    private val camApsFxController = CamApsFxController()
    private lateinit var homeAssistantController: HomeAssistantController

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: Service creating")
        mainStateProvider.addLog("Service creating")
        homeAssistantController = HomeAssistantController(this)
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
                            homeAssistantController.update(state)
                        }
                        is CamApsFxState.Error -> Unit // TODO
                    }
                }
        }

        scope.launch {
            ReinitializationManager.onSuccess.collectLatest {
                Log.d(TAG, "Re-initializing Home Assistant connection")
                mainStateProvider.addLog("Re-initializing Home Assistant connection")
                homeAssistantController.start()
            }
        }
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

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Service is running in the background")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        scope.launch {
            mainStateProvider.setServiceState(MainServiceState.Connected)
            var success = false
            while (!success) {
                try {
                    homeAssistantController.start()
                    success = true
                } catch (e: Exception) {
                    mainStateProvider.addLog("Failed to connect to Home Assistant. Retrying in 10 minutes.")
                    delay(600_000) // 10 minutes
                }
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
        camApsFxController.handleNotification(this, statusBarNotification)
    }

    companion object {

        private val TAG = MainService::class.java.simpleName
        private const val NOTIFICATION_CHANNEL_ID = "com.faltenreich.camaps.background_service"
        private const val NOTIFICATION_ID = 1
    }
}