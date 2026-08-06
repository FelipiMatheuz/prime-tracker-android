package com.felipimatheuz.primehunt.ui.screen.splash.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.EtlFile
import com.felipimatheuz.primehunt.domain.model.SyncEvent

@Composable
fun TextSyncAnimation(syncEvent: SyncEvent){

    AnimatedContent(syncEvent) {
        when (it) {
            SyncEvent.Starting -> SplashSyncStatus(R.string.starting_sync)

            is SyncEvent.CheckingManifest -> SplashSyncStatus(R.string.check_manifest)

            is SyncEvent.Downloading -> SplashSyncStatus(
                R.string.downloading_content,
                it.file
            )

            is SyncEvent.Importing -> SplashSyncStatus(
                R.string.importing_content,
                it.file
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
            }
        }
    }

}

@Composable
private fun SplashSyncStatus(textRes: Int, file: EtlFile? = null) {

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