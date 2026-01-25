package com.faltenreich.camaps

import android.content.Context
import com.faltenreich.camaps.core.data.KeyValueStore
import com.faltenreich.camaps.service.homeassistant.HomeAssistantController
import com.faltenreich.camaps.screen.settings.SettingsRepository

object ServiceLocator {

    lateinit var settingsRepository: SettingsRepository
    lateinit var homeAssistantController: HomeAssistantController

    fun setup(context: Context) {
        val keyValueStore = KeyValueStore(context)
        settingsRepository = SettingsRepository(keyValueStore)
        homeAssistantController = HomeAssistantController(settingsRepository)
    }
}