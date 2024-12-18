package com.faltenreich.camaps

sealed interface MainServiceState {

    data object Disconnected : MainServiceState

    data object Connected : MainServiceState
}