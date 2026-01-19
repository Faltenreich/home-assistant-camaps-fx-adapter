package com.faltenreich.camaps.settings

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ReinitializationManager {

    private val _onSuccess = MutableSharedFlow<Unit>()
    val onSuccess = _onSuccess.asSharedFlow()

    suspend fun reinitialize() {
        _onSuccess.emit(Unit)
    }
}