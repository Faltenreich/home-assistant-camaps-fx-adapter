package com.faltenreich.camaps.camaps

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.RemoteViews
import com.faltenreich.camaps.BuildConfig
import com.faltenreich.camaps.homeassistant.HomeAssistantClient
import com.faltenreich.camaps.homeassistant.device.HomeAssistantRegisterDeviceRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantRegisterSensorRequestBody
import com.faltenreich.camaps.homeassistant.sensor.HomeAssistantUpdateSensorRequestBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.ArrayList
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class CamApsFxNotificationListenerService : NotificationListenerService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val homeAssistantClient = HomeAssistantClient.local()

    private var componentName: ComponentName? = null
    private var webhookId: String? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        scope.launch {
            val registerDeviceRequestBody = HomeAssistantRegisterDeviceRequestBody(
                deviceId = "deviceId", // TODO: Find unique and consistent identifier?
                appId = BuildConfig.APPLICATION_ID,
                appName = "CamAPS FX Adapter",
                appVersion = BuildConfig.VERSION_NAME,
                deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
                manufacturer = Build.MANUFACTURER,
                model = Build.MODEL,
                osName = "Android",
                osVersion = Build.VERSION.SDK_INT.toString(),
                supportsEncryption = false,
            )
            Log.d(TAG, "Registering device: $registerDeviceRequestBody")
            val registerDeviceResponse = try {
                homeAssistantClient.registerDevice(registerDeviceRequestBody)
            } catch (exception: Exception) {
                Log.e(TAG, "Registering device failed: $exception")
                return@launch
            }
            Log.d(TAG, "Registered device: $registerDeviceResponse")

            // FIXME: HTTP 200 but no sensor is visible in home assistant
            val registerSensorRequestBody = HomeAssistantRegisterSensorRequestBody(
                data = HomeAssistantRegisterSensorRequestBody.Data(
                    state = 120f,
                ),
            )
            Log.d(TAG, "Registering sensor: $registerSensorRequestBody")
            val registerSensorResponse = try {
                homeAssistantClient.registerSensor(
                    requestBody = registerSensorRequestBody,
                    webhookId = registerDeviceResponse.webhookId,
                )
            } catch (exception: Exception) {
                Log.e(TAG, "Registering sensor failed: $exception")
                return@launch
            }
            Log.d(TAG, "Registered sensor: $registerSensorResponse")

            webhookId = registerDeviceResponse.webhookId

            updateSensor(120f)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        return super.onBind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        job.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStartCommand")

        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }

        componentName?.let {
            requestRebind(it)
            toggleNotificationListenerService(it)
        }
        return START_REDELIVER_INTENT
    }

    private fun toggleNotificationListenerService(componentName: ComponentName) {
        val pm = packageManager
        pm.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        pm.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "onListenerConnected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "onListenerDisconnected")

        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }

        componentName?.let { requestRebind(it) }
    }

    @Suppress("DEPRECATION")
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Notification
        val statusBarNotification = sbn?.takeIf { it.packageName == "com.camdiab.fx_alert.mgdl" }
        val notification = statusBarNotification?.notification ?: return
        val contentView = notification.contentView ?: return

        // RemoteViews
        val actionsProperty = RemoteViews::class.memberProperties.first { it.name == "mActions" }
        actionsProperty.isAccessible = true
        val actions = actionsProperty.get(contentView) as ArrayList<*>

        // CamAPS FX
        val action = actions[2]// TODO: Get action for setText with numeric String als value
        val valueProperty = action::class.memberProperties.first { it.name == "value" }
        valueProperty.isAccessible = true
        val value = valueProperty.getter.call(action)

        Log.d(TAG, "onNotificationPosted: $value mg/dL")

        updateSensor(120f)
    }

    private fun updateSensor(value: Float) = scope.launch {
        val webhookId = webhookId ?: run {
            Log.d(TAG, "Skipping update due to missing webhook")
            return@launch
        }
        val updateSensorRequestBody = HomeAssistantUpdateSensorRequestBody(
            data = HomeAssistantUpdateSensorRequestBody.Data(
                state = value,
            ),
        )
        Log.d(TAG, "Updating sensor: $updateSensorRequestBody")
        val updateSensorResponse = try {
            homeAssistantClient.updateSensor(
                requestBody = updateSensorRequestBody,
                webhookId = webhookId,
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Updating sensor failed: $exception")
            return@launch
        }
        Log.d(TAG, "Updated sensor: $updateSensorResponse")
    }

    companion object {

        private val TAG = CamApsFxNotificationListenerService::class.java.simpleName
    }
}