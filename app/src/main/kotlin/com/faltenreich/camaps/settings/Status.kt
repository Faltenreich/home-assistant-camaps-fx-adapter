package com.faltenreich.camaps.settings

sealed interface Status {

    data object Loading : Status

    data class Success(val message: String) : Status

    data class Failure(val message: String) : Status
}