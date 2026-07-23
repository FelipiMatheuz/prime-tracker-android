package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.state.EtlFile
import com.felipimatheuz.primehunt.business.state.SyncEvent
import com.felipimatheuz.primehunt.ui.component.AnimatedLoad
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.viewmodel.SplashViewModel

@Composable
fun SplashScreen(onReady: () -> Unit, viewModel: SplashViewModel = hiltViewModel()) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (loadItem, bottomLogo) = createRefs()
        Column(modifier = Modifier.constrainAs(loadItem) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, horizontalAlignment = Alignment.CenterHorizontally) {
            val syncEvent = viewModel.syncEvent.collectAsState()
            when (val event = syncEvent.value) {
                SyncEvent.Starting -> {
                    ShowLoading(R.string.starting_sync)
                }

                is SyncEvent.CheckingManifest -> {
                    ShowLoading(R.string.check_manifest)
                }

                is SyncEvent.Downloading -> {
                    ShowLoading(R.string.downloading_content, event.file)
                }

                is SyncEvent.Importing -> {
                    ShowLoading(R.string.importing_content, event.file)
                }

                is SyncEvent.Success -> {
                    ShowLoading(R.string.sync_success)
                    LaunchedEffect(Unit) {
                        onReady()
                    }
                }

                is SyncEvent.AlreadyUpToDate -> {
                    ShowLoading(R.string.sync_up_to_date)
                    LaunchedEffect(Unit) {
                        onReady()
                    }
                }

                is SyncEvent.Error -> {
                    ShowError { onReady() }
                }
            }
        }

        Row(
            modifier = Modifier
                .constrainAs(bottomLogo) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.by_owner),
                Modifier.padding(end = 8.dp),
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
private fun ShowLoading(textRes: Int, file: EtlFile? = null) {
    AnimatedLoad()

    val finalText = if (file == null) {
        stringResource(textRes)
    } else {
        stringResource(textRes, file.text)
    }

    Text(
        text = finalText, style = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun ShowError(onReady: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage = stringResource(R.string.sync_failed)

    LaunchedEffect(Unit) {
        snackbarHostState.showSnackbar(
            message = errorMessage,
            duration = SnackbarDuration.Short
        )
        onReady()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                modifier = Modifier.padding(12.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.wifi_off),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = data.visuals.message)
                }
            }
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    PrimeTrackerTheme {
        SplashScreen({})
    }
}