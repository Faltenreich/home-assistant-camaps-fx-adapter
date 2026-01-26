package com.faltenreich.camaps

data class MainState(
    val permission: Permission,
) {

    sealed interface Permission {

        data object Loading : Permission

        data object Granted : Permission

        data object Denied : Permission
    }
}