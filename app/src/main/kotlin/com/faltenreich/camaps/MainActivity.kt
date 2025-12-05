package com.faltenreich.camaps

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.faltenreich.camaps.navigation.NavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: Activity starting")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                NavGraph()
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}