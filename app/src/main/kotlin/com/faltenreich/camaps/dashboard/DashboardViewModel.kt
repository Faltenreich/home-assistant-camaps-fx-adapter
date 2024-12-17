package com.faltenreich.camaps.dashboard

import androidx.lifecycle.ViewModel
import com.faltenreich.camaps.MainStateHolder

class DashboardViewModel : ViewModel() {

    val state = MainStateHolder.state
}