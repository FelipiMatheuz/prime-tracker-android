package com.felipimatheuz.primehunt.ui.screen.squadfinder.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.enums.AppTheme
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.domain.model.squad.SquadAnnouncement
import com.felipimatheuz.primehunt.ui.util.TargetLocalizationHelper
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.screen.components.PrimePanel
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@Composable
fun SquadActiveAnnouncementContent(
    ownAnnouncement: SquadAnnouncement,
    isConnected: Boolean,
    onStopAnnouncement: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showStopConfirmDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PrimePanel(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.squad_my_active_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                if (isConnected) stringResource(R.string.notification_squad_connected)
                                else stringResource(R.string.notification_squad_disconnected)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isConnected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.errorContainer
                            }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val targetType = TargetLocalizationHelper.determineTargetType(ownAnnouncement.targetId, ownAnnouncement.targetName)
                val internationalizedName = TargetLocalizationHelper.getLocalizedTargetName(ownAnnouncement.targetName, targetType, ownAnnouncement.targetId)
                val isDefaultLanguage = internationalizedName.replace(":", "") == ownAnnouncement.targetName

                Text(
                    text = stringResource(R.string.squad_my_target_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = internationalizedName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!isDefaultLanguage) {
                    Text(
                        text = "(${ownAnnouncement.targetName})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = { },
                        label = { Text(stringResource(ownAnnouncement.platform.labelRes)) }
                    )
                    AssistChip(
                        onClick = { },
                        label = { Text(stringResource(ownAnnouncement.region.labelRes)) }
                    )
                }

                if (!ownAnnouncement.note.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(
                            R.string.squad_card_note_label,
                            ownAnnouncement.note
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                val stopButtonInteraction = remember { MutableInteractionSource() }
                Button(
                    onClick = { showStopConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .pressScale(stopButtonInteraction, PressIntensity.MODERATE)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.squad_my_stop_button),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (showStopConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showStopConfirmDialog = false },
                title = { Text(stringResource(R.string.squad_my_stop_dialog_title)) },
                text = { Text(stringResource(R.string.squad_my_stop_dialog_desc)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showStopConfirmDialog = false
                            onStopAnnouncement()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.squad_my_stop_dialog_confirm),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStopConfirmDialog = false }) {
                        Text(stringResource(R.string.squad_my_stop_dialog_cancel))
                    }
                }
            )
        }
    }
}

@Preview(name = "Squad Active Announcement - Light")
@Composable
fun SquadActiveAnnouncementPreview() {
    PrimeTrackerTheme {
        Surface {
            SquadActiveAnnouncementContent(
                ownAnnouncement = SquadAnnouncement(
                    id = "1",
                    userId = "1",
                    targetId = "ash_prime",
                    targetName = "Ash Prime Set",
                    nickname = "TennoPrime",
                    platform = Platform.PC,
                    region = Region.SOUTH_AMERICA,
                    createdAt = System.currentTimeMillis(),
                    expiresAt = System.currentTimeMillis() + 300000,
                    note = "Radshare 4/4",
                    isMine = true,
                    isExpired = false
                ),
                isConnected = true,
                onStopAnnouncement = {}
            )
        }
    }
}

@Preview(name = "Squad Active Announcement - Dark")
@Composable
fun SquadActiveAnnouncementDarkPreview() {
    PrimeTrackerTheme(theme = AppTheme.DARK) {
        Surface {
            SquadActiveAnnouncementContent(
                ownAnnouncement = SquadAnnouncement(
                    id = "1",
                    userId = "1",
                    targetId = "ash_prime",
                    targetName = "Ash Prime Set",
                    nickname = "TennoPrime",
                    platform = Platform.PC,
                    region = Region.SOUTH_AMERICA,
                    createdAt = System.currentTimeMillis(),
                    expiresAt = System.currentTimeMillis() + 300000,
                    note = "Radshare 4/4",
                    isMine = true,
                    isExpired = false
                ),
                isConnected = false,
                onStopAnnouncement = {}
            )
        }
    }
}
