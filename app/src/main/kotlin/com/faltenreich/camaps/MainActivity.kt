package com.faltenreich.camaps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.faltenreich.camaps.settings.KeyValueStore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        KeyValueStore.setup(this)

        setContent {
            MainScreen()
        }
    }
}