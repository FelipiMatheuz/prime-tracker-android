package com.felipimatheuz.primehunt.ui.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.felipimatheuz.primehunt.ui.screen.overview.orbital.OrbitalCarousel
import com.felipimatheuz.primehunt.ui.screen.overview.orbital.rememberOrbitalState
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@Composable
fun OverviewScreen(
    padding: PaddingValues = PaddingValues()
) {
    val state = rememberOrbitalState()

    OrbitalCarousel(
        state = state,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    )
}

@Preview(showBackground = true)
@Composable
fun OverviewScreenPreview() {
    PrimeTrackerTheme {
        OverviewScreen()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun OverviewScreenDarkPreview() {
    PrimeTrackerTheme {
        OverviewScreen()
    }
}