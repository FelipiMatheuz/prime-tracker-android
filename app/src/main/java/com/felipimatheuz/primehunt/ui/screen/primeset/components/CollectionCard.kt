package com.felipimatheuz.primehunt.ui.screen.primeset.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.PrimeCollection
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.ui.theme.Completed
import com.felipimatheuz.primehunt.ui.theme.InProgress
import com.felipimatheuz.primehunt.ui.theme.NotStarted

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CollectionCard(
    collection: PrimeCollection,
    modifier: Modifier = Modifier,
    onSetClick: (PrimeSetDomain) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "arrowRotation")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(4.dp, NotStarted),
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

                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    label = "borderProgressAnimation"
                )

                val targetProgressColor = when {
                    completedSets == collection.sets.size && collection.sets.isNotEmpty() -> Completed
                    completedSets > 0 -> InProgress
                    else -> MaterialTheme.colorScheme.primary
                }

                val animatedProgressColor by animateColorAsState(
                    targetValue = targetProgressColor,
                    animationSpec = tween(300),
                    label = "progressColorAnimation"
                )

                val targetTextColor = when {
                    completedSets == collection.sets.size && collection.sets.isNotEmpty() -> Completed
                    completedSets > 0 -> InProgress
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                val animatedTextColor by animateColorAsState(
                    targetValue = targetTextColor,
                    animationSpec = tween(300),
                    label = "textColorAnimation"
                )

                val borderWidth = 4.dp

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .drawWithCache {
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

                            onDrawBehind {
                                if (animatedProgress > 0f) {
                                    val segmentPath = Path()
                                    pathMeasure.getSegment(
                                        0f,
                                        totalLength * animatedProgress,
                                        segmentPath,
                                        true
                                    )

                                    drawPath(
                                        path = segmentPath,
                                        color = animatedProgressColor,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
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
                        color = animatedTextColor
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
