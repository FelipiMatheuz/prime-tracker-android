package com.felipimatheuz.primehunt.ui.screen.primeset.primedetail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.model.PrimePartDomain
import com.felipimatheuz.primehunt.domain.model.RelicRewardDomain
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.theme.getColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailComponentItem(
    part: PrimePartDomain,
    isArchwing: Boolean = false,
    isNested: Boolean = false,
    onUpdateQuantity: (Int) -> Unit,

    ) {
    val isSet = part.name == PrimePartType.PRIME_SET
    val setName = part.id.replace("_", " ").replaceFirstChar { it.uppercase() }
    val isArchwingSystems = part.name == PrimePartType.SYSTEMS && isArchwing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isNested) 48.dp else 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(if (isSet) 56.dp else 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSet) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (isSet && part.imageUrl != null) {
                AsyncImage(
                    model = part.imageUrl,
                    contentDescription = stringResource(R.string.generic_icon_description, setName),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(R.drawable.ic_orokin),
                    error = painterResource(R.drawable.ic_orokin),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val imgIconRes =
                    if (isSet) R.drawable.ic_prime else if (isArchwingSystems) R.drawable.prime_circuit else part.name.icon
                Image(
                    painter = painterResource(id = imgIconRes),
                    contentDescription = stringResource(R.string.generic_icon_description, setName),
                    modifier = Modifier.size(if (isSet) 40.dp else 32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isSet) setName else stringResource(part.name.text),
                style = if (isSet) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSet) MaterialTheme.colorScheme.primary else Color.Unspecified
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (!isSet && part.relics.isNotEmpty()) {
                if (part.availableRelics.isNotEmpty()) {
                    RelicGroup(
                        relics = part.availableRelics, modifier = Modifier.background(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                            RoundedCornerShape(4.dp)
                        )
                    )
                }

                if (part.vaultedRelics.isNotEmpty()) {
                    RelicGroup(relics = part.vaultedRelics)
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        if (!isSet) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHighest,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(4.dp)
            ) {
                val downButtonInteraction = remember { MutableInteractionSource() }
                val upButtonInteraction = remember { MutableInteractionSource() }

                IconButton(
                    onClick = { onUpdateQuantity(-1) },
                    interactionSource = downButtonInteraction,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_down_arrow),
                        contentDescription = stringResource(R.string.minus_description),
                        modifier = Modifier
                            .size(16.dp)
                            .pressScale(downButtonInteraction, PressIntensity.MODERATE)
                    )
                }

                Text(
                    text = "${part.ownedQuantity}/${part.neededQuantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = { onUpdateQuantity(1) },
                    interactionSource = upButtonInteraction,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_up_arrow),
                        contentDescription = stringResource(R.string.plus_description),
                        modifier = Modifier
                            .size(16.dp)
                            .pressScale(upButtonInteraction, PressIntensity.MODERATE)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RelicGroup(relics: List<RelicRewardDomain>, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        FlowRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            relics.forEach { relic ->
                Text(
                    text = relic.name,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = relic.rarity.getColor(),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
