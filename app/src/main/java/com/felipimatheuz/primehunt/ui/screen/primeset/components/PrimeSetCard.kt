package com.felipimatheuz.primehunt.ui.screen.primeset.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.enums.RelicSource
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.theme.Vault
import com.felipimatheuz.primehunt.ui.theme.Completed
import com.felipimatheuz.primehunt.ui.theme.InProgress
import com.felipimatheuz.primehunt.ui.theme.NotStarted

@Composable
fun PrimeSetCard(
    primeSet: PrimeSetDomain,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cardInteraction = remember { MutableInteractionSource() }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource = cardInteraction, role = Role.Button) { onClick() }
            .pressScale(cardInteraction, PressIntensity.VERY_SUBTLE),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, NotStarted, RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp))
            ) {
                AsyncImage(
                    model = primeSet.imageUrl,
                    contentDescription = stringResource(R.string.generic_image_description, primeSet.name),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(R.drawable.ic_orokin),
                    error = painterResource(R.drawable.ic_orokin),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = primeSet.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (primeSet.isNested) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(R.drawable.ic_nested),
                            contentDescription = stringResource(R.string.dependency_image_description),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(primeSet.type.displayNameRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val availabilityColor = when (primeSet.availability) {
                        RelicSource.MISSION -> MaterialTheme.colorScheme.onSurfaceVariant
                        RelicSource.RESURGENCE, RelicSource.BARO -> MaterialTheme.colorScheme.primary
                        RelicSource.VAULT -> Vault
                    }

                    Text(
                        text = stringResource(primeSet.availability.displayNameRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = availabilityColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                val textColor = when {
                    primeSet.ownedPieces == primeSet.totalPieces && primeSet.totalPieces > 0 -> Completed
                    primeSet.ownedPieces > 0 -> InProgress
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                AnimatedVisibility(textColor == Completed) {
                    Image(
                        painter = painterResource(id = R.drawable.mastery_rank),
                        contentDescription = stringResource(R.string.mastery_image_description),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "${primeSet.ownedPieces}/${primeSet.totalPieces}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }
}
