package com.faltenreich.camaps.dashboard.camaps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.faltenreich.camaps.R
import com.faltenreich.camaps.camaps.BloodSugar
import com.faltenreich.camaps.camaps.CamApsFxState
import com.faltenreich.camaps.dashboard.DashboardTitle

@Composable
fun CamApsFxDashboard(
    state: CamApsFxState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.background(
            when (state) {
                is CamApsFxState.None,
                is CamApsFxState.Value-> Color.Transparent
                is CamApsFxState.Error -> Color.Red
            }
        ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DashboardTitle(
            text = stringResource(R.string.cam_aps_fx),
            painter = painterResource(R.drawable.ic_camaps_fx),
        )
        when (state) {
            is CamApsFxState.None -> Text("-")
            is CamApsFxState.Value -> {
                Text(state.bloodSugar.mgDl.toString())
                Text(
                    when (state.bloodSugar.trend) {
                        BloodSugar.Trend.RISING_FAST -> "+++"
                        BloodSugar.Trend.RISING -> "++"
                        BloodSugar.Trend.RISING_SLOW -> "+"
                        BloodSugar.Trend.STEADY -> ""
                        BloodSugar.Trend.DROPPING_SLOW -> "-"
                        BloodSugar.Trend.DROPPING -> "--"
                        BloodSugar.Trend.DROPPING_FAST -> "---"
                        null -> ""
                    }
                )
            }
            is CamApsFxState.Error -> Text("ERROR: ${state.message}")
        }
    }
}

@Preview
@Composable
private fun Preview(
    @PreviewParameter(CamApsFxStatePreviewParameterProvider ::class)
    state: CamApsFxState,
) {
    CamApsFxDashboard(state)
}