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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.enums.AppTheme
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.domain.model.squad.SquadAnnouncement
import com.felipimatheuz.primehunt.domain.model.squad.UserProfile
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.screen.components.PrimePanel
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquadMyAnnouncementContent(
    userProfile: UserProfile?,
    ownAnnouncement: SquadAnnouncement?,
    availableGoals: List<GoalDomain>,
    isConnected: Boolean,
    onCreateAnnouncement: (targetId: String, note: String?) -> Unit,
    onStopAnnouncement: () -> Unit,
    onNavigateToProfile: () -> Unit,
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
        if (userProfile == null) {
            PrimePanel(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_squad),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.squad_my_profile_required_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.squad_my_profile_required_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val profileButtonInteraction = remember { MutableInteractionSource() }
                    Button(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.pressScale(profileButtonInteraction, PressIntensity.MODERATE)
                    ) {
                        Text(stringResource(R.string.squad_my_profile_setup_button))
                    }
                }
            }
            return@Column
        }

        if (ownAnnouncement != null) {
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

                    Text(
                        text = stringResource(R.string.squad_my_target_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = ownAnnouncement.targetName.ifEmpty { ownAnnouncement.targetId },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

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
                            text = stringResource(R.string.squad_card_note_label, ownAnnouncement.note),
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

            return@Column
        }

        Text(
            text = stringResource(R.string.squad_my_new_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        var selectedGoal by remember { mutableStateOf(availableGoals.firstOrNull()) }
        var customNote by remember { mutableStateOf("") }
        var dropdownExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = !dropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedGoal?.targetName ?: stringResource(R.string.squad_my_select_goal),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.squad_my_goal_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                if (availableGoals.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.squad_my_no_goals)) },
                        onClick = { dropdownExpanded = false }
                    )
                } else {
                    availableGoals.forEach { goal ->
                        DropdownMenuItem(
                            text = { Text(goal.targetName) },
                            onClick = {
                                selectedGoal = goal
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = customNote,
            onValueChange = { if (it.length <= 100) customNote = it },
            label = { Text(stringResource(R.string.squad_my_note_label)) },
            placeholder = { Text(stringResource(R.string.squad_my_note_placeholder)) },
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        PrimePanel(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.squad_my_inherited_profile_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${stringResource(R.string.squad_card_nick_label)}${userProfile.nickname}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${stringResource(R.string.squad_profile_platform_label)}: ${stringResource(userProfile.platform.labelRes)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${stringResource(R.string.squad_profile_region_label)}: ${stringResource(userProfile.region.labelRes)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        val publishButtonInteraction = remember { MutableInteractionSource() }
        Button(
            onClick = {
                selectedGoal?.let { goal ->
                    onCreateAnnouncement(goal.targetId, customNote.ifBlank { null })
                }
            },
            enabled = selectedGoal != null && isConnected,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .pressScale(publishButtonInteraction, PressIntensity.MODERATE)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_target),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.squad_my_publish_button),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(name = "Squad My Announcement - Active")
@Composable
fun SquadMyAnnouncementActivePreview() {
    PrimeTrackerTheme {
        Surface {
            SquadMyAnnouncementContent(
                userProfile = UserProfile("1", "TennoPrime", Platform.PC, Region.SOUTH_AMERICA),
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
                availableGoals = emptyList(),
                isConnected = true,
                onCreateAnnouncement = { _, _ -> },
                onStopAnnouncement = {},
                onNavigateToProfile = {}
            )
        }
    }
}

@Preview(name = "Squad My Announcement - Create Dark")
@Composable
fun SquadMyAnnouncementCreatePreview() {
    PrimeTrackerTheme(theme = AppTheme.DARK) {
        Surface {
            SquadMyAnnouncementContent(
                userProfile = UserProfile("1", "TennoPrime", Platform.PC, Region.SOUTH_AMERICA),
                ownAnnouncement = null,
                availableGoals = emptyList(),
                isConnected = true,
                onCreateAnnouncement = { _, _ -> },
                onStopAnnouncement = {},
                onNavigateToProfile = {}
            )
        }
    }
}
