package com.felipimatheuz.primehunt.ui.screen.relic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.domain.model.RelicComponentDomain
import com.felipimatheuz.primehunt.domain.model.RelicDomain
import com.felipimatheuz.primehunt.ui.screen.components.PrimePanel
import com.felipimatheuz.primehunt.ui.theme.Complete
import com.felipimatheuz.primehunt.ui.theme.High

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
                RelicSource.VAULT -> Complete
            }

            Text(
                text = availabilityText,
                style = MaterialTheme.typography.labelMedium,
                color = availabilityColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        
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
                    text = stringResource(R.string.relic_tracked_indicator, relic.trackedCount),
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
                        color = High,
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

            if (reward.trackingTags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    reward.trackingTags.forEach { tagRes ->
                        TrackingTag(stringResource(tagRes))
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackingTag(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontWeight = FontWeight.Bold
        )
    }
}
