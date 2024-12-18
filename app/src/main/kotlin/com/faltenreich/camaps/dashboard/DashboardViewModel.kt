package com.faltenreich.camaps.dashboard

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faltenreich.camaps.MainService
import com.faltenreich.camaps.MainStateProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DashboardViewModel : ViewModel() {

    private val hasPermissions = MutableStateFlow<Boolean>(false)
    private val log = MainStateProvider.state.map { it.log }

    val state = combine(
        hasPermissions,
        log,
    ) { hasPermissions, log ->
        if (hasPermissions) DashboardState.Content(log)
        else DashboardState.MissingPermissions
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = DashboardState.MissingPermissions,
    )

    fun checkPermissions(context: Context) {
        val componentName = ComponentName(context, MainService::class.java)
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners",
        )
        hasPermissions.update {
            enabledListeners?.contains(componentName.flattenToString()) == true
        }
    }

    fun openNotificationSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        activity.startActivityForResult(intent, 1001)
    }
}