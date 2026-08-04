package com.felipimatheuz.primehunt.ui.screen.relic.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.local.enums.GoalIcons
import com.felipimatheuz.primehunt.data.remote.enums.DropRarity
import com.felipimatheuz.primehunt.data.remote.enums.RelicEra
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.domain.model.RelicComponentDomain
import com.felipimatheuz.primehunt.domain.model.RelicDomain
import com.felipimatheuz.primehunt.ui.theme.Vault
import com.felipimatheuz.primehunt.ui.theme.Completed
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.theme.White

@Composable
fun RelicCard(
    relic: RelicDomain,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val availabilityColor = when (relic.source) {
        RelicSource.MISSION -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        RelicSource.RESURGENCE, RelicSource.BARO -> MaterialTheme.colorScheme.primary
        RelicSource.VAULT -> Vault
    }

    Box(
        modifier = modifier
            .width(120.dp)
            .padding(top = 20.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth().height(100.dp)
                .clickable { onClick() }
                .border(3.dp, availabilityColor, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${relic.era.displayName} ${relic.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    if (relic.hasForma) {
                        Icon(
                            painter = painterResource(R.drawable.ic_forma),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (relic.isCompleted) {
                    Text(
                        text = stringResource(R.string.relic_completed),
                        style = MaterialTheme.typography.bodySmall,
                        color = Completed,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = stringResource(R.string.relic_missing_indicator, relic.missingCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (relic.goalCount > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.ic_target),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = relic.goalCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
        ) {
            val glowColor = MaterialTheme.colorScheme.primary
            val glowIntensity = (relic.missingCount.coerceAtMost(5) / 5f)
            val isDark = isSystemInDarkTheme()
            val eraPainter = painterResource(relic.era.icon)

            Image(
                painter = eraPainter,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .drawGlow(
                        painter = eraPainter,
                        color = glowColor,
                        alpha = if (isDark) glowIntensity * 0.6f else glowIntensity * 0.4f,
                        radius = 8.dp * glowIntensity
                    )
            )
        }
    }
}

fun Modifier.drawGlow(
    painter: Painter,
    color: Color,
    alpha: Float,
    radius: Dp
) = this.drawBehind {
    if (alpha > 0f) {
        val glowPx = radius.toPx()
        val drawSize = Size(size.width + glowPx * 2, size.height + glowPx * 2)

        translate(left = -glowPx, top = -glowPx) {
            with(painter) {
                draw(
                    size = drawSize,
                    alpha = alpha,
                    colorFilter = ColorFilter.tint(color)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RelicCardPreview() {
    PrimeTrackerTheme {
        RelicCard(
            relic = RelicDomain(
                "meso_Z99", "Z99", RelicEra.MESO, RelicSource.MISSION, listOf(
                    RelicComponentDomain(
                        "teste", DropRarity.COMMON, false,
                        goalTags = listOf(
                            GoalTagDomain(0, "teste", GoalIcons.PRIMARY, White),
                            GoalTagDomain(1, "teste", GoalIcons.SECONDARY, White)
                        )
                    ),
                    RelicComponentDomain("teste", DropRarity.COMMON, false),
                    RelicComponentDomain("", DropRarity.COMMON, false, isForma = true),
                    RelicComponentDomain("teste", DropRarity.COMMON, false)
                )
            )
        ) {}
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun RelicCardDarkPreview() {
    PrimeTrackerTheme {
        RelicCard(
            relic = RelicDomain(
                "meso_Z99", "Z99", RelicEra.MESO, RelicSource.MISSION, listOf(
                    RelicComponentDomain(
                        "teste", DropRarity.COMMON, false,
                        goalTags = listOf()
                    ),
                    RelicComponentDomain("teste", DropRarity.COMMON, false),
                    RelicComponentDomain("", DropRarity.COMMON, false, isForma = true),
                    RelicComponentDomain("teste", DropRarity.COMMON, false)
                )
            )
        ) {}
    }
}
