package com.felipimatheuz.primehunt.ui.screen.relic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.local.enums.GoalIcons
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.domain.model.RelicComponentDomain
import com.felipimatheuz.primehunt.domain.model.RelicDomain
import com.felipimatheuz.primehunt.ui.screen.components.GoalTagChip
import com.felipimatheuz.primehunt.ui.screen.components.PrimePanel
import com.felipimatheuz.primehunt.ui.theme.Vault
import com.felipimatheuz.primehunt.ui.theme.Completed

@Composable
fun RelicDetailsDialog(
    relic: RelicDomain,
    onDismiss: () -> Unit
) {
    AnimatedRelicDialog(
        relicEraIcon = relic.era.icon,
        onDismiss = onDismiss
    ) { closeDialog  ->
        RelicDetailsContent(
            relic = relic,
            onDismiss = closeDialog
        )
    }
}

@Composable
private fun RelicDetailsContent(
    relic: RelicDomain,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top = 16.dp)
    ) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${relic.era.displayName} ${relic.name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            val availabilityText = when (relic.source) {
                RelicSource.MISSION -> stringResource(R.string.relic_origin_current)
                RelicSource.RESURGENCE -> stringResource(R.string.relic_origin_resurgence)
                RelicSource.BARO -> stringResource(R.string.relic_origin_baro)
                RelicSource.VAULT -> stringResource(R.string.relic_origin_vaulted)
            }

            val availabilityColor = when (relic.source) {
                RelicSource.MISSION -> MaterialTheme.colorScheme.onSurfaceVariant
                RelicSource.RESURGENCE, RelicSource.BARO -> MaterialTheme.colorScheme.primary
                RelicSource.VAULT -> Vault
            }

            Text(
                text = availabilityText,
                style = MaterialTheme.typography.labelMedium,
                color = availabilityColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            relic.goalTags.take(5).forEach { tag ->
                GoalTagChip(
                    iconRes = tag.icon.icon,
                    color = tag.color,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
        
        // Summary
        PrimePanel {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.relic_missing_indicator, relic.missingCount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.relic_tracked_indicator, relic.goalCount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Drops
        val sortedRewards = remember(relic.rewards) {
            relic.rewards.sortedWith(
                compareByDescending<RelicComponentDomain> { it.rarity.ordinal }
                    .thenBy { it.name }
            )
        }
        
        Box(modifier = Modifier.weight(1f, fill = false)) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(sortedRewards) { reward ->
                    RelicRewardItem(reward)
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Close button
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = stringResource(R.string.close),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RelicRewardItem(reward: RelicComponentDomain) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(reward.rarity.getColor())
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = reward.rarity.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = if (reward.isForma) {
                stringResource(R.string.forma_blueprint)
            } else {
                reward.nameSuffixRes?.let { suffix ->
                    "${reward.name} ${stringResource(suffix)}"
                } ?: reward.name
            },
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (reward.isObtained) {
                    Text(
                        text = stringResource(R.string.complete),
                        style = MaterialTheme.typography.bodySmall,
                        color = Completed,
                        fontWeight = FontWeight.Bold
                    )
                } else if (!reward.isForma) {
                    Text(
                        text = stringResource(R.string.relic_needed_indicator, reward.neededQuantity),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                reward.compositeInfo?.let { info ->
                    Text(
                        text = stringResource(R.string.relic_requires_label, info),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            if (reward.goalTags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val displayTags = reward.goalTags.take(2)
                    val remaining = reward.goalTags.size - 2

                    displayTags.forEach { tag ->
                        GoalTagChip(
                            text = tag.name,
                            color = tag.color,
                            iconRes = tag.icon.icon
                        )
                    }

                    if (remaining > 0) {
                        GoalTagChip(
                            text = "+$remaining",
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RelicDetailsDialogPreview() {
    val mockRelic = RelicDomain(
        id = "lith_v1",
        name = "V1",
        era = com.felipimatheuz.primehunt.data.remote.enums.RelicEra.LITH,
        source = RelicSource.MISSION,
        goalTags = listOf(
            GoalTagDomain(10, "Vaulted", GoalIcons.SLASH, Color(0xFFE91E63)),
            GoalTagDomain(11, "Tracked", GoalIcons.VIRAL, Color(0xFFFF9800))
        ),
        rewards = listOf(
            RelicComponentDomain(
                name = "Valkyr Prime Chassis",
                rarity = com.felipimatheuz.primehunt.data.remote.enums.DropRarity.RARE,
                isObtained = false,
                neededQuantity = 1,
                goalTags = listOf(
                    GoalTagDomain(1, "Warframe", GoalIcons.SLASH, Color(0xFF673AB7)),
                    GoalTagDomain(2, "Set", GoalIcons.VIRAL, Color(0xFF2196F3)),
                    GoalTagDomain(3, "Extra", GoalIcons.VOID, Color(0xFF4CAF50))
                )
            )
        )
    )

    MaterialTheme {
        Surface {
            RelicDetailsContent(
                relic = mockRelic,
                onDismiss = {}
            )
        }
    }
}
