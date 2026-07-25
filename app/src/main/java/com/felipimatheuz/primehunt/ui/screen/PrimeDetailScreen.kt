package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.model.PrimePartDomain
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.model.RelicRewardDomain
import com.felipimatheuz.primehunt.ui.theme.High
import com.felipimatheuz.primehunt.ui.theme.Low
import com.felipimatheuz.primehunt.ui.theme.Zero
import com.felipimatheuz.primehunt.viewmodel.PrimeDetailIntent
import com.felipimatheuz.primehunt.viewmodel.PrimeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun PrimeDetailScreen(
    padding: PaddingValues,
    setId: String,
    onBack: () -> Unit
) {
    val viewModel: PrimeDetailViewModel = hiltViewModel(
        key = setId,
        creationCallback = { factory: PrimeDetailViewModel.Factory ->
            factory.create(setId)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            state.primeSet?.let { set ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        HeaderSection(set)
                    }

                    item {
                        QuickActions(
                            onAddSet = { viewModel.onIntent(PrimeDetailIntent.UpdateSetQuantity(1)) },
                            onRemoveSet = { viewModel.onIntent(PrimeDetailIntent.UpdateSetQuantity(-1)) }
                        )
                    }

                    set.parts.forEach { part ->
                        item(key = part.id) {
                            ComponentItem(part) { delta ->
                                viewModel.onIntent(PrimeDetailIntent.UpdateQuantity(part.id, delta))
                            }
                        }

                        if (part.nestedParts.isNotEmpty()) {
                            items(
                                part.nestedParts,
                                key = { "nested_${part.id}_${it.id}" }) { nested ->
                                ComponentItem(nested, isNested = true) { delta ->
                                    viewModel.onIntent(
                                        PrimeDetailIntent.UpdateQuantity(
                                            nested.id,
                                            delta
                                        )
                                    )
                                }
                            }
                        }

                        item {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cross),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun HeaderSection(primeSet: PrimeSetDomain) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(200.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            GlideImage(
                model = primeSet.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = primeSet.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(primeSet.type.displayNameRes),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        val progress =
            if (primeSet.totalPieces > 0) primeSet.ownedPieces.toFloat() / primeSet.totalPieces else 0f

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(8.dp)
                .clip(CircleShape),
            color = if (progress == 1f) High else if (progress > 0) Low else Zero
        )
        Text(
            text = stringResource(R.string.detail_pieces_owned_template, primeSet.ownedPieces, primeSet.totalPieces),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun QuickActions(
    onAddSet: () -> Unit,
    onRemoveSet: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onRemoveSet,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(stringResource(R.string.detail_action_remove_set))
        }
        Button(
            onClick = onAddSet,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.detail_action_add_set))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalGlideComposeApi::class)
@Composable
private fun ComponentItem(
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
                    painter = painterResource(id = if (isSet) R.drawable.ic_lato_prime else part.name.icon),
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
