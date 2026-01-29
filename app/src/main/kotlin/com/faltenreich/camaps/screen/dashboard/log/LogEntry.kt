package com.faltenreich.camaps.screen.dashboard.log

data class LogEntry(
    val dateTime: String,
    val source: String,
    val message: String,
    val issue: Issue? = null,
) {

    enum class Issue {

        MISSING_PERMISSION,
    }
}
