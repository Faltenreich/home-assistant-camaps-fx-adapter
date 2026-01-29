package com.faltenreich.camaps

import android.content.Context
import com.faltenreich.camaps.core.data.KeyValueStore
import com.faltenreich.camaps.screen.login.SettingsRepository
import com.faltenreich.camaps.service.camaps.CamApsFxController
import com.faltenreich.camaps.service.camaps.CamApsFxPackageLocator
import com.faltenreich.camaps.service.camaps.notification.CamApsFxNotificationMapper
import com.faltenreich.camaps.service.homeassistant.HomeAssistantController

object ServiceLocator {

    lateinit var appStateProvider: AppStateProvider private set
    lateinit var settingsRepository: SettingsRepository private set
    lateinit var homeAssistantController: HomeAssistantController private set
    lateinit var camApsFxController: CamApsFxController private set
    lateinit var camApsFxPackageLocator: CamApsFxPackageLocator private set

    fun setup(context: Context) {
        if (::appStateProvider.isInitialized) { return }

        appStateProvider = AppStateProvider()
        settingsRepository = SettingsRepository(
            keyValueStore = KeyValueStore(context),
        )
        homeAssistantController = HomeAssistantController(
            appStateProvider = appStateProvider,
            settingsRepository = settingsRepository,
        )
        camApsFxController = CamApsFxController(
            appStateProvider = appStateProvider,
            mapNotification = CamApsFxNotificationMapper(),
        )
        camApsFxPackageLocator = CamApsFxPackageLocator(context)
    }
}