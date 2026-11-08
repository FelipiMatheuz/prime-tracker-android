package com.felipimatheuz.primehunt.ui.screen.squadfinder.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.domain.model.squad.SquadAnnouncement
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.screen.components.PrimePanel
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@Composable
fun SquadCard(
    announcement: SquadAnnouncement,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    PrimePanel(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Target Name Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_target),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = announcement.targetName.ifEmpty { announcement.targetId },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Live status indicator
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = if (announcement.isMine) stringResource(R.string.squad_card_my_announcement)
                            else stringResource(R.string.squad_card_active),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (announcement.isMine) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nickname & Copy Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.squad_card_nick_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = announcement.nickname,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                val copyInteraction = remember { MutableInteractionSource() }
                val nicknameCopiedMsg = stringResource(R.string.squad_card_nickname_copied, announcement.nickname)
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Warframe Nickname", announcement.nickname)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, nicknameCopiedMsg, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .pressScale(copyInteraction, PressIntensity.SUBTLE)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = stringResource(R.string.squad_card_copy_nickname),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Platform & Region Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val platformLabel = stringResource(announcement.platform.labelRes)
                val regionLabel = stringResource(announcement.region.labelRes)

                AssistChip(
                    onClick = { },
                    label = { Text(platformLabel, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                AssistChip(
                    onClick = { },
                    label = { Text(regionLabel, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            // Note (if present)
            if (!announcement.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.squad_card_note_label, announcement.note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Squad Card - Light")
@Composable
fun SquadCardPreview() {
    PrimeTrackerTheme {
        Surface {
            SquadCard(
                announcement = SquadAnnouncement(
                    id = "user1",
                    userId = "user1",
                    targetId = "ash_prime",
                    targetName = "Ash Prime Set",
                    nickname = "TennoPrime",
                    platform = Platform.PC,
                    region = Region.SOUTH_AMERICA,
                    createdAt = System.currentTimeMillis(),
                    expiresAt = System.currentTimeMillis() + 300000,
                    note = "Radshare 4/4 - Rápido",
                    isMine = false,
                    isExpired = false
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Squad Card - Dark")
@Composable
fun SquadCardDarkPreview() {
    PrimeTrackerTheme(theme = com.felipimatheuz.primehunt.domain.model.enums.AppTheme.DARK) {
        Surface {
            SquadCard(
                announcement = SquadAnnouncement(
                    id = "user2",
                    userId = "user2",
                    targetId = "lith_a1",
                    targetName = "Relic Lith A1",
                    nickname = "ExcaliburMain",
                    platform = Platform.CROSSPLAY,
                    region = Region.NORTH_AMERICA,
                    createdAt = System.currentTimeMillis(),
                    expiresAt = System.currentTimeMillis() + 300000,
                    note = null,
                    isMine = true,
                    isExpired = false
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
