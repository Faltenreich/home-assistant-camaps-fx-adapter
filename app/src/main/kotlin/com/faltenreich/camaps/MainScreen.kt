package com.faltenreich.camaps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.camaps.CamApsFxStateObserver

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val state = CamApsFxStateObserver.state.collectAsStateWithLifecycle().value

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is CamApsFxState.None -> Text("No event")
            is CamApsFxState.Value -> Text(state.bloodSugar.mgDl.toString())
            is CamApsFxState.Error -> Text("ERROR: ${state.message}")
        }
    }
}