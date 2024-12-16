package com.faltenreich.camaps.adapter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object BloodSugarEventAdapter {

    private val _events = MutableStateFlow<BloodSugarEvent?>(null)
    val events = _events.asStateFlow()

    fun postEvent(event: BloodSugarEvent) {
        _events.tryEmit(event)
    }
}