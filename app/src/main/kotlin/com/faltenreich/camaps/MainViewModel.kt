package com.faltenreich.camaps

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.screen.settings.SettingsRepository
import com.faltenreich.camaps.service.MainService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    settingsRepository: SettingsRepository = ServiceLocator.settingsRepository,
) : ViewModel() {

    val state = combine(
        settingsRepository.getHomeAssistantUri(),
        settingsRepository.getHomeAssistantToken(),
    ) { (uri, token) ->
        if (uri != null && token != null) MainState.Authenticated
        else MainState.Unauthenticated
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = MainState.Loading,
    )

    fun checkPermissions(context: Context) {
        val componentName = ComponentName(context, MainService::class.java)
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners",
        )
        val hasPermission = enabledListeners?.contains(componentName.flattenToString()) == true
    }

    fun openNotificationSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
    }

    companion object {

        private const val ACTIVITY_REQUEST_CODE = 1001
    }
}