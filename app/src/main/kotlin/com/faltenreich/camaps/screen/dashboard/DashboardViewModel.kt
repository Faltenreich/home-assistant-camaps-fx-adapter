package com.faltenreich.camaps.screen.dashboard

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.AppStateProvider
import com.faltenreich.camaps.R
import com.faltenreich.camaps.ServiceLocator.appStateProvider
import com.faltenreich.camaps.locate
import com.faltenreich.camaps.screen.dashboard.log.LogEntryFactory
import com.faltenreich.camaps.screen.login.SettingsRepository
import com.faltenreich.camaps.service.MainService
import com.faltenreich.camaps.service.MainServiceState
import com.faltenreich.camaps.service.camaps.CamApsFxPackageLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    appStateProvider: AppStateProvider = locate(),
    private val settingsRepository: SettingsRepository = locate(),
    private val camApsFxPackageLocator: CamApsFxPackageLocator = locate(),
) : ViewModel() {

    val state = appStateProvider.log
        .map(::DashboardState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = DashboardState(log = emptyList()),
        )

    fun checkApps() {
        if (!camApsFxPackageLocator.isCamApsFxAppInstalled()) {
            appStateProvider.addLog(LogEntryFactory.create(MainServiceState.MissingApp))
        }
    }

    fun installApp(context: Context) {
        val url = context.getString(R.string.cam_aps_fx_app_url)
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun checkPermissions(context: Context) {
        val componentName = ComponentName(context, MainService::class.java)
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners",
        )
        val hasPermission = enabledListeners?.contains(componentName.flattenToString()) == true
        if (!hasPermission) {
            appStateProvider.addLog(LogEntryFactory.create(MainServiceState.MissingPermission))
        }
    }

    fun openNotificationSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
    }

    fun logout() = viewModelScope.launch {
        settingsRepository.saveHomeAssistantUri("")
        settingsRepository.saveHomeAssistantToken("")
    }

    companion object {

        private const val ACTIVITY_REQUEST_CODE = 1001
    }
}