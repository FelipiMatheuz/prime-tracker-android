package com.felipimatheuz.primehunt.ui.screen.cloud.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudState

@Composable
fun CloudBackupCard(
    state: CloudState,
    onUploadClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEnabled = state.isAuthenticated && !state.isLoading
    val alpha = if (state.isAuthenticated) 1f else 0.5f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.cloud_backup_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.cloud_backup_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val uploadInteraction = remember { MutableInteractionSource() }
                Button(
                    onClick = onUploadClick,
                    interactionSource = uploadInteraction,
                    enabled = isEnabled && !state.isDownloading,
                    modifier = Modifier
                        .weight(1f)
                        .pressScale(
                            uploadInteraction,
                            PressIntensity.VERY_SUBTLE,
                            isEnabled && !state.isDownloading
                        )
                ) {
                    if (state.isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(R.string.cloud_backup_upload))
                    }
                }
                val downloadInteraction = remember { MutableInteractionSource() }
                OutlinedButton(
                    onClick = onDownloadClick,
                    interactionSource = downloadInteraction,
                    enabled = isEnabled && !state.isUploading,
                    modifier = Modifier
                        .weight(1f)
                        .pressScale(
                            downloadInteraction,
                            PressIntensity.VERY_SUBTLE,
                            isEnabled && !state.isUploading
                        )
                ) {
                    if (state.isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(stringResource(R.string.cloud_backup_download))
                    }
                }
            }
        }
    }
}
