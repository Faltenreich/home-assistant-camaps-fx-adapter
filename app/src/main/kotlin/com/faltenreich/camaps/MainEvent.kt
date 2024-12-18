package com.faltenreich.camaps

sealed interface MainEvent {

    data object ToggleService : MainEvent
}