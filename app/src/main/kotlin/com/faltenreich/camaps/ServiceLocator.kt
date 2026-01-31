package com.faltenreich.camaps

import android.content.Context
import com.faltenreich.camaps.core.data.KeyValueStore
import com.faltenreich.camaps.core.data.SettingsRepository
import com.faltenreich.camaps.service.camaps.CamApsFxController
import com.faltenreich.camaps.service.camaps.CamApsFxPackageLocator
import com.faltenreich.camaps.service.camaps.notification.CamApsFxNotificationMapper
import com.faltenreich.camaps.service.homeassistant.HomeAssistantController

object ServiceLocator {

    lateinit var appStateProvider: AppStateProvider
    lateinit var settingsRepository: SettingsRepository
    lateinit var homeAssistantController: HomeAssistantController
    lateinit var camApsFxController: CamApsFxController
    lateinit var camApsFxPackageLocator: CamApsFxPackageLocator

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

inline fun <reified T> locate(): T = with(ServiceLocator) {
    when (T::class) {
        AppStateProvider::class -> appStateProvider
        SettingsRepository::class -> settingsRepository
        HomeAssistantController::class -> homeAssistantController
        CamApsFxController::class -> camApsFxController
        CamApsFxPackageLocator::class -> camApsFxPackageLocator
        else -> error("")
    } as T
}