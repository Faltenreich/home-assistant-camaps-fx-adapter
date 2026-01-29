package com.faltenreich.camaps.screen.dashboard

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.AppStateProvider
import com.faltenreich.camaps.ServiceLocator
import com.faltenreich.camaps.service.MainService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DashboardViewModel(
    appStateProvider: AppStateProvider = ServiceLocator.appStateProvider,
) : ViewModel() {

    private val log = appStateProvider.log
    private val hasPermission = MutableStateFlow(false)

    val state = combine(
        log,
        hasPermission,
    ) { log, hasPermission ->
        if (hasPermission) DashboardState.Content(log)
        else DashboardState.MissingPermission
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = DashboardState.Loading,
    )

    fun checkPermissions(context: Context) {
        val componentName = ComponentName(context, MainService::class.java)
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners",
        )
        hasPermission.update { enabledListeners?.contains(componentName.flattenToString()) == true }
    }

    fun openNotificationSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
    }

    companion object {

        private const val ACTIVITY_REQUEST_CODE = 1001
    }
}