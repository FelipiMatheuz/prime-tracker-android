package com.felipimatheuz.primehunt.ui.screen.squadfinder.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.felipimatheuz.primehunt.ui.util.TargetLocalizationHelper
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.enums.AppTheme
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.domain.model.squad.UserProfile
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquadCreateAnnouncementContent(
    userProfile: UserProfile?,
    availableGoals: List<GoalDomain>,
    isConnected: Boolean,
    onCreateAnnouncement: (targetId: String, note: String?) -> Unit,
    onNavigateToProfile: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.squad_my_new_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 40.dp)
            )

            var selectedGoal by remember { mutableStateOf(availableGoals.firstOrNull()) }
            var customNote by remember { mutableStateOf("") }
            var dropdownExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedGoalText = selectedGoal?.let {
                    TargetLocalizationHelper.getLocalizedTargetName(it.targetName, it.targetType, it.targetId)
                } ?: stringResource(R.string.squad_my_select_goal)

                OutlinedTextField(
                    value = selectedGoalText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.squad_my_goal_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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
                            val localizedGoalName = TargetLocalizationHelper.getLocalizedTargetName(goal.targetName, goal.targetType, goal.targetId)
                            DropdownMenuItem(
                                text = { Text(localizedGoalName) },
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

            if (userProfile == null) {
                ProfileRequiredCard(
                    onNavigateToProfile = onNavigateToProfile
                )
            } else {
                ProfileDataCard(
                    userProfile = userProfile,
                    onNavigateToProfile = onNavigateToProfile
                )
            }

            val publishButtonInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {
                    selectedGoal?.let { goal ->
                        onCreateAnnouncement(goal.targetId, customNote.ifBlank { null })
                    }
                },
                enabled = selectedGoal != null && userProfile != null && isConnected,
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

        val closeInteraction = remember { MutableInteractionSource() }
        IconButton(
            onClick = onClose,
            interactionSource = closeInteraction,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd)
                .pressScale(closeInteraction, PressIntensity.SUBTLE)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = stringResource(R.string.close),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(name = "Squad Create Announcement - Light")
@Composable
fun SquadCreateAnnouncementPreview() {
    PrimeTrackerTheme {
        Surface {
            SquadCreateAnnouncementContent(
                userProfile = UserProfile("1", "TennoPrime", Platform.PC, Region.SOUTH_AMERICA),
                availableGoals = emptyList(),
                isConnected = true,
                onCreateAnnouncement = { _, _ -> },
                onNavigateToProfile = {},
                onClose = {}
            )
        }
    }
}

@Preview(name = "Squad Create Announcement - Dark")
@Composable
fun SquadCreateAnnouncementDarkPreview() {
    PrimeTrackerTheme(theme = AppTheme.DARK) {
        Surface {
            SquadCreateAnnouncementContent(
                userProfile = null,
                availableGoals = emptyList(),
                isConnected = true,
                onCreateAnnouncement = { _, _ -> },
                onNavigateToProfile = {},
                onClose = {}
            )
        }
    }
}
