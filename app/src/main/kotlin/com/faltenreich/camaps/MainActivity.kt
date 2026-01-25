package com.faltenreich.camaps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.faltenreich.camaps.core.data.KeyValueStore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KeyValueStore.setup(this)

        enableEdgeToEdge()

        setContent {
            MainScreen()
        }
    }
}