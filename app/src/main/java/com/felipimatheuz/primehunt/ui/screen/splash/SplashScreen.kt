package com.felipimatheuz.primehunt.ui.screen.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.state.SyncEvent
import com.felipimatheuz.primehunt.ui.screen.splash.components.AnimatedLoad
import com.felipimatheuz.primehunt.ui.screen.splash.components.SplashFooter
import com.felipimatheuz.primehunt.ui.screen.splash.components.SplashSyncStatus
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.viewmodel.splash.SplashViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen(onReady: () -> Unit, viewModel: SplashViewModel = hiltViewModel()) {
    val syncEvent = viewModel.syncEvent.collectAsState()
    SplashContent(onReady, syncEvent.value)
}

@Composable
fun SplashContent(onReady: () -> Unit, syncEvent: SyncEvent) {

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedLoad(syncEvent is SyncEvent.Error)
            when (syncEvent) {
                SyncEvent.Starting -> SplashSyncStatus(R.string.starting_sync)

                is SyncEvent.CheckingManifest -> SplashSyncStatus(R.string.check_manifest)

                is SyncEvent.Downloading -> SplashSyncStatus(
                    R.string.downloading_content,
                    syncEvent.file
                )

                is SyncEvent.Importing -> SplashSyncStatus(
                    R.string.importing_content,
                    syncEvent.file
                )

                is SyncEvent.Success, SyncEvent.AlreadyUpToDate, SyncEvent.Error -> {
                    val textRes = when (syncEvent) {
                        is SyncEvent.Success -> {
                            R.string.sync_success
                        }

                        is SyncEvent.AlreadyUpToDate -> {
                            R.string.sync_up_to_date
                        }

                        else -> {
                            R.string.sync_failed
                        }
                    }
                    SplashSyncStatus(textRes)
                    LaunchedEffect(Unit) {
                        delay(1.seconds)
                        onReady()
                    }
                }
            }
        }
        SplashFooter(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    PrimeTrackerTheme {
        SplashContent({}, SyncEvent.Error)
    }
}