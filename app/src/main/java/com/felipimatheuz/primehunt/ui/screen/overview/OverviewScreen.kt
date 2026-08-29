package com.felipimatheuz.primehunt.ui.screen.overview

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felipimatheuz.primehunt.domain.model.enums.BgIcons
import com.felipimatheuz.primehunt.ui.screen.overview.cards.*
import com.felipimatheuz.primehunt.ui.viewmodel.overview.OverviewState
import com.felipimatheuz.primehunt.ui.screen.overview.orbital.OrbitalCarousel
import com.felipimatheuz.primehunt.ui.screen.overview.orbital.OrbitalState
import com.felipimatheuz.primehunt.ui.screen.overview.orbital.OverviewBackground
import com.felipimatheuz.primehunt.ui.screen.overview.orbital.rememberOrbitalState
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.viewmodel.AppSettingsViewModel
import com.felipimatheuz.primehunt.ui.viewmodel.overview.OverviewViewModel

@Composable
fun OverviewScreen(
    padding: PaddingValues = PaddingValues(),
    viewModel: OverviewViewModel = hiltViewModel(),
    settingsViewModel: AppSettingsViewModel = hiltViewModel()
) {
    val state = rememberOrbitalState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedWallpaper by settingsViewModel.selectedWallpaper.collectAsStateWithLifecycle()

    OverviewContent(
        padding = padding,
        orbitalState = state,
        uiState = uiState,
        iconRes = selectedWallpaper.icon
    )
}

@Composable
fun OverviewContent(
    padding: PaddingValues,
    orbitalState: OrbitalState,
    uiState: OverviewState,
    iconRes: Int = 0
) {
    Box(modifier = Modifier.fillMaxSize()) {
        OverviewBackground(
            state = orbitalState,
            iconRes = iconRes
        )

        OrbitalCarousel(
            state = orbitalState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            pages = listOf(
                { PrimeSetsOverviewCard(uiState.primeSets) },
                { DatabaseOverviewCard(uiState.database) },
                { RelicsOverviewCard(uiState.relics) },
                { GoalsOverviewCard(uiState.goals) },
                { TradeOverviewCard(uiState.trade) }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OverviewScreenPreview() {
    PrimeTrackerTheme {
        OverviewContent(
            padding = PaddingValues(),
            orbitalState = rememberOrbitalState(),
            uiState = OverviewState(),
            iconRes = BgIcons.WARFRAME.icon
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun OverviewScreenDarkPreview() {
    PrimeTrackerTheme {
        OverviewContent(
            padding = PaddingValues(),
            orbitalState = rememberOrbitalState(),
            uiState = OverviewState(),
            iconRes = BgIcons.WARFRAME.icon
        )
    }
}