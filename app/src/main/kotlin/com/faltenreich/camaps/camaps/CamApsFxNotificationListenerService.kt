package com.faltenreich.camaps.camaps

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.RemoteViews
import com.faltenreich.camaps.BuildConfig
import com.faltenreich.camaps.homeassistant.HomeAssistantApi
import com.faltenreich.camaps.homeassistant.NetworkClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
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
    private val accessToken = BuildConfig.HOME_ASSISTANT_TOKEN

    private val homeAssistantApi = HomeAssistantApi(
        host = "http://homeassistant.local:8123",
        client = NetworkClient(
            httpClient = HttpClient(OkHttp) {
                install(ContentNegotiation) { json() }
                install(Auth) {
                    bearer {
                        loadTokens {
                            BearerTokens(
                                accessToken = accessToken,
                                refreshToken = null,
                            )
                        }
                    }
                }
            },
        ),
    )

    private var componentName: ComponentName? = null

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            homeAssistantApi.register()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

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
    }

    companion object {

        private val TAG = CamApsFxNotificationListenerService::class.java.simpleName
    }
}