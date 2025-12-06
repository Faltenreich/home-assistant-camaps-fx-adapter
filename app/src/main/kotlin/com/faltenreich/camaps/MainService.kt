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
import com.faltenreich.camaps.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainService : NotificationListenerService() {

    private val mainStateProvider = MainStateProvider
    private val camApsFxController = CamApsFxController()
    private lateinit var homeAssistantController: HomeAssistantController
    private lateinit var settingsRepository: SettingsRepository

    private val scope = CoroutineScope(Dispatchers.IO)
    private var notificationTimeoutJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: Service creating")
        mainStateProvider.addLog("Service creating")
        homeAssistantController = HomeAssistantController(this)
        settingsRepository = SettingsRepository(this)
        scope.launch {
            ReinitializationManager.onSuccess.collect {
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

        createNotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT)
        createNotificationChannel(NOTIFICATION_TIMEOUT_CHANNEL_ID, getString(R.string.notification_timeout_channel_name), NotificationManager.IMPORTANCE_HIGH)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Service is running in the background")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        scope.launch {
            mainStateProvider.setServiceState(MainServiceState.Connected)
            try {
                homeAssistantController.start()
            } catch (e: Exception) {
                mainStateProvider.addLog("Failed to connect to Home Assistant. Retrying in 10 minutes.")
                delay(600_000) // 10 minutes
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
            notificationTimeoutJob?.cancel()
            scope.launch {
                homeAssistantController.update(state)

                val timeoutMinutes = settingsRepository.getNotificationTimeoutMinutes()
                if (timeoutMinutes > 0) {
                    notificationTimeoutJob = scope.launch {
                        delay(timeoutMinutes * 60 * 1000L)
                        sendTimeoutNotification(timeoutMinutes)
                    }
                }
            }
        }
    }

    private fun createNotificationChannel(channelId: String, name: String, importance: Int) {
        val channel = NotificationChannel(channelId, name, importance)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendTimeoutNotification(timeoutMinutes: Int) {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_TIMEOUT_CHANNEL_ID)
            .setContentTitle("No New Readings")
            .setContentText("No new readings received in the last $timeoutMinutes minutes.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_TIMEOUT_ID, notification)
    }

    companion object {

        private val TAG = MainService::class.java.simpleName
        private const val NOTIFICATION_CHANNEL_ID = "com.faltenreich.camaps.background_service"
        private const val NOTIFICATION_TIMEOUT_CHANNEL_ID = "com.faltenreich.camaps.timeout_notification"
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_TIMEOUT_ID = 2
    }
}