package com.felipimatheuz.primehunt.ui.screen.cloud

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.felipimatheuz.primehunt.ui.screen.cloud.components.CloudAccountCard
import com.felipimatheuz.primehunt.ui.screen.cloud.components.CloudBackupCard
import com.felipimatheuz.primehunt.ui.screen.cloud.components.DangerZoneCard
import com.felipimatheuz.primehunt.ui.screen.cloud.components.DiagnosticsCard
import com.felipimatheuz.primehunt.ui.screen.cloud.components.LegacyMigrationCard
import com.felipimatheuz.primehunt.ui.theme.InProgress
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudIntent
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudSideEffect
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudViewModel
import java.io.File

@Composable
fun CloudScreen(
    padding: PaddingValues,
    viewModel: CloudViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var isError by remember { mutableStateOf(false) }

    val message = viewModel.getPromptMessage(
        state.actionResult,
        onSuccess = { isError = false },
        onError = { isError = true }
    )

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CloudSideEffect.ShareLogFile -> {
                    val file = File(context.cacheDir, "prime_tracker_logs.txt")
                    file.writeText(effect.logContent)
                    val contentUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    val chooser = Intent.createChooser(intent, "Send Logs")
                    context.startActivity(chooser)
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (message.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.onSecondary.copy(0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = message,
                        color = if (isError) InProgress else MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            CloudAccountCard(
                state = state,
                onSignInClick = { viewModel.onIntent(CloudIntent.SignIn) },
                onSignOutClick = { viewModel.onIntent(CloudIntent.SignOut) }
            )
        }

        item {
            CloudBackupCard(
                state = state,
                onUploadClick = { viewModel.onIntent(CloudIntent.UploadBackup) },
                onDownloadClick = { viewModel.onIntent(CloudIntent.DownloadBackup) }
            )
        }

        item {
            LegacyMigrationCard(
                state = state,
                onMigrateClick = { viewModel.onIntent(CloudIntent.StartMigration) }
            )
        }

        item {
            DiagnosticsCard(
                state = state,
                onSendLogsClick = { viewModel.onIntent(CloudIntent.SendLogs) }
            )
        }

        item {
            DangerZoneCard(
                state = state,
                onClearCloudDataClick = { viewModel.onIntent(CloudIntent.ClearCloudData) }
            )
        }
    }
}
