package com.faltenreich.camaps.service.camaps

import android.content.Context
import android.content.pm.PackageManager

class CamApsFxPackageLocator(context: Context) {

    private val packageManager = context.packageManager

    fun isCamApsFxAppInstalled(): Boolean {
        return CAM_APS_FX_PACKAGE_NAMES.any(::isAppInstalled)
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    companion object {

        const val CAM_APS_FX_PACKAGE_NAME_PREFIX = "com.camdiab.fx_alert"
        private val CAM_APS_FX_PACKAGE_NAMES = listOf(
            "$CAM_APS_FX_PACKAGE_NAME_PREFIX.mgdl",
            "$CAM_APS_FX_PACKAGE_NAME_PREFIX.mmoll",
        )
    }
}