package com.faltenreich.camaps

import android.content.Context
import com.faltenreich.camaps.core.data.KeyValueStore
import com.faltenreich.camaps.screen.settings.SettingsRepository
import com.faltenreich.camaps.service.camaps.CamApsFxController
import com.faltenreich.camaps.service.camaps.notification.CamApsFxNotificationMapper
import com.faltenreich.camaps.service.homeassistant.HomeAssistantController

object ServiceLocator {

    lateinit var appStateProvider: AppStateProvider private set
    lateinit var settingsRepository: SettingsRepository private set
    lateinit var camApsFxController: CamApsFxController private set
    lateinit var homeAssistantController: HomeAssistantController private set

    fun setup(context: Context) {
        appStateProvider = AppStateProvider()
        settingsRepository = SettingsRepository(
            keyValueStore = KeyValueStore(context),
        )
        camApsFxController = CamApsFxController(
            appStateProvider = appStateProvider,
            mapNotification = CamApsFxNotificationMapper(),
        )
        homeAssistantController = HomeAssistantController(
            appStateProvider = appStateProvider,
            settingsRepository = settingsRepository,
        )
    }
}