package com.faltenreich.camaps.dashboard

import androidx.lifecycle.ViewModel
import com.faltenreich.camaps.MainStateProvider

class DashboardViewModel : ViewModel() {

    private val mainStateProvider = MainStateProvider

    val state = mainStateProvider.state
}