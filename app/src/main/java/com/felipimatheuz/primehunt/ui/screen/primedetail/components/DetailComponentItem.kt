package com.felipimatheuz.primehunt.ui.screen.primedetail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.model.PrimePartDomain
import com.felipimatheuz.primehunt.domain.model.RelicRewardDomain

@OptIn(ExperimentalLayoutApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun DetailComponentItem(
    part: PrimePartDomain,
    isNested: Boolean = false,
    onUpdateQuantity: (Int) -> Unit
) {
    val isSet = part.name == PrimePartType.PRIME_SET

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
                GlideImage(
                    model = part.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = if (isSet) R.drawable.ic_prime else part.name.icon),
                    contentDescription = null,
                    modifier = Modifier.size(if (isSet) 40.dp else 32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isSet) part.id.replace("_", " ").replaceFirstChar { it.uppercase() }
                else stringResource(part.name.text),
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
                IconButton(
                    onClick = { onUpdateQuantity(-1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_down_arrow),
                        contentDescription = "Minus",
                        modifier = Modifier.size(16.dp)
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
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_up_arrow),
                        contentDescription = "Add",
                        modifier = Modifier.size(16.dp)
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
