package com.faltenreich.camaps.core.ui

sealed interface Status {

    data object None : Status

    data object Loading : Status

    data class Success(val message: String) : Status

    data class Failure(val message: String) : Status
}