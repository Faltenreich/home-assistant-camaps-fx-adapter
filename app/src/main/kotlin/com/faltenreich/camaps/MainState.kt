package com.faltenreich.camaps

sealed interface MainState {

    data object Loading : MainState

    data object Unauthenticated : MainState

    data object Authenticated : MainState
}