package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.state.EtlFile
import com.felipimatheuz.primehunt.business.state.SyncEvent
import com.felipimatheuz.primehunt.ui.component.AnimatedLoad
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.viewmodel.SplashViewModel
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
                SyncEvent.Starting -> ShowStatusText(R.string.starting_sync)

                is SyncEvent.CheckingManifest -> ShowStatusText(R.string.check_manifest)

                is SyncEvent.Downloading -> ShowStatusText(
                    R.string.downloading_content,
                    syncEvent.file
                )

                is SyncEvent.Importing -> ShowStatusText(R.string.importing_content, syncEvent.file)

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
                    ShowStatusText(textRes)
                    LaunchedEffect(Unit) {
                        delay(1.seconds)
                        onReady()
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.by_owner),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Image(
                painterResource(R.drawable.cs_logo),
                contentDescription = stringResource(R.string.logo)
            )
        }
    }
}


@Composable
private fun ShowStatusText(textRes: Int, file: EtlFile? = null) {

    val finalText = if (file == null) {
        stringResource(textRes)
    } else {
        stringResource(textRes, file.text)
    }

    Text(
        text = finalText,
        modifier = Modifier.padding(horizontal = 24.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    PrimeTrackerTheme {
        SplashContent({}, SyncEvent.Error)
    }
}