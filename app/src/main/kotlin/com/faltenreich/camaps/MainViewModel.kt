package com.faltenreich.camaps

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import com.faltenreich.camaps.service.MainService

class MainViewModel : ViewModel() {

    val state = MainStateProvider.state

    fun checkPermissions(context: Context) {
        val componentName = ComponentName(context, MainService::class.java)
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners",
        )
        val hasPermission = enabledListeners?.contains(componentName.flattenToString()) == true
        val permissionState = if (hasPermission) MainState.Permission.Granted else MainState.Permission.Denied
        MainStateProvider.setPermissionState(permissionState)
    }

    fun openNotificationSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
    }

    companion object {

        private const val ACTIVITY_REQUEST_CODE = 1001
    }
}