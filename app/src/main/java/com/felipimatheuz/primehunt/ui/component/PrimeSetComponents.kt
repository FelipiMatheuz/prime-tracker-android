package com.felipimatheuz.primehunt.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.domain.model.PrimeCollection
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.ui.theme.Complete
import com.felipimatheuz.primehunt.ui.theme.High
import com.felipimatheuz.primehunt.ui.theme.Low
import com.felipimatheuz.primehunt.ui.theme.Zero

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PrimeSetCard(
    primeSet: PrimeSetDomain,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                GlideImage(
                    model = primeSet.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
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
                            painter = painterResource(R.drawable.link_comp),
                            contentDescription = "Dependency",
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
                    
                    val availabilityColor = when(primeSet.availability) {
                        RelicSource.MISSION -> MaterialTheme.colorScheme.onSurfaceVariant
                        RelicSource.RESURGENCE, RelicSource.BARO -> MaterialTheme.colorScheme.primary
                        RelicSource.VAULT -> Complete
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

            Column(horizontalAlignment = Alignment.End) {
                val textColor = when {
                    primeSet.ownedPieces == primeSet.totalPieces && primeSet.totalPieces > 0 -> High
                    primeSet.ownedPieces > 0 -> Low
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CollectionCard(
    collection: PrimeCollection,
    onSetClick: (PrimeSetDomain) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "arrowRotation")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(4.dp, Zero),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val completedSets =
                    collection.sets.count { it.ownedPieces == it.totalPieces && it.totalPieces > 0 }
                val progress =
                    if (collection.sets.isNotEmpty()) completedSets.toFloat() / collection.sets.size else 0f

                val progressColor = when {
                    completedSets == collection.sets.size && collection.sets.isNotEmpty() -> High
                    completedSets > 0 -> Low
                    else -> MaterialTheme.colorScheme.primary
                }

                val textColor = when {
                    completedSets == collection.sets.size && collection.sets.isNotEmpty() -> High
                    completedSets > 0 -> Low
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                val borderWidth = 4.dp

                Box(
                    modifier = Modifier
                        .size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (progress > 0f) {
                            val strokeWidth = borderWidth.toPx()
                            val cornerRadius = 12.dp.toPx()
                            val path = Path().apply {
                                addRoundRect(
                                    RoundRect(
                                        left = strokeWidth / 2,
                                        top = strokeWidth / 2,
                                        right = size.width - strokeWidth / 2,
                                        bottom = size.height - strokeWidth / 2,
                                        cornerRadius = CornerRadius(cornerRadius)
                                    )
                                )
                            }

                            val pathMeasure = PathMeasure()
                            pathMeasure.setPath(path, false)
                            val totalLength = pathMeasure.length
                            val segmentPath = Path()
                            pathMeasure.getSegment(0f, totalLength * progress, segmentPath, true)

                            drawPath(
                                path = segmentPath,
                                color = progressColor,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(borderWidth)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        GlideImage(
                            model = collection.promoImage,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    val collectionName = if (collection.name.isEmpty()) {
                        stringResource(R.string.prime_no_collection)
                    } else {
                        stringResource(R.string.prime_collection_template, collection.name)
                    }
                    Text(
                        text = collectionName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = stringResource(
                            R.string.sets_completed_template,
                            completedSets,
                            collection.sets.size
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }

                Icon(
                    painterResource(R.drawable.ic_show_hide),
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    collection.sets.forEach { set ->
                        PrimeSetCard(
                            primeSet = set,
                            modifier = Modifier.padding(vertical = 4.dp),
                            onClick = { onSetClick(set) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryHeader(
    titleRes: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(titleRes),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}
