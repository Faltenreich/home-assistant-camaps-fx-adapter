package com.faltenreich.camaps.service

sealed interface MainServiceState {

    data object Disconnected : MainServiceState

    data object Connected : MainServiceState
}