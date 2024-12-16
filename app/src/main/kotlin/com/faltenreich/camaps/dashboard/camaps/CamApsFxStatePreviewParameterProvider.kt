package com.faltenreich.camaps.dashboard.camaps

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.faltenreich.camaps.camaps.BloodSugar
import com.faltenreich.camaps.camaps.CamApsFxState

class CamApsFxStatePreviewParameterProvider : PreviewParameterProvider<CamApsFxState> {

    override val values = sequenceOf(
        CamApsFxState.None,
        CamApsFxState.Value(bloodSugar = BloodSugar(mgDl = 120f, trend = BloodSugar.Trend.STEADY)),
        CamApsFxState.Error(message = "Something went wrong"),
    )
}