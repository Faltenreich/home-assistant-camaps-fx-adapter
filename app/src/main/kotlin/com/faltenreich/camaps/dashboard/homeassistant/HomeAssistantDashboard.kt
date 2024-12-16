package com.faltenreich.camaps.dashboard.homeassistant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.faltenreich.camaps.R
import com.faltenreich.camaps.dashboard.DashboardArrow
import com.faltenreich.camaps.dashboard.DashboardComponent
import com.faltenreich.camaps.dashboard.DashboardTitle
import com.faltenreich.camaps.homeassistant.HomeAssistantState

@Composable
fun HomeAssistantDashboard(
    state: HomeAssistantState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DashboardTitle(
            text = stringResource(R.string.home_assistant),
            painter = painterResource(R.drawable.ic_home_assistant),
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DashboardArrow()
            DashboardComponent(painterResource(R.drawable.ic_home_assistant))
        }
    }
}

@Preview
@Composable
private fun Preview(
    @PreviewParameter(HomeAssistantStatePreviewParameterProvider::class)
    state: HomeAssistantState,
) {
    HomeAssistantDashboard(state)
}