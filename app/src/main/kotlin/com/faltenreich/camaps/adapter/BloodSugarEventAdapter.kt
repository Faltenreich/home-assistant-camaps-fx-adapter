package com.faltenreich.camaps.adapter

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object BloodSugarEventAdapter {

    private val _events = MutableSharedFlow<BloodSugarEvent>()
    val events = _events.asSharedFlow()

    fun postEvent(event: BloodSugarEvent) {
        _events.tryEmit(event)
    }
}