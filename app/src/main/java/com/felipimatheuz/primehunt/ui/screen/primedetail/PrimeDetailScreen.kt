package com.felipimatheuz.primehunt.ui.screen.primedetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.screen.components.LoadingUI
import com.felipimatheuz.primehunt.ui.screen.primedetail.components.DetailComponentItem
import com.felipimatheuz.primehunt.ui.screen.primedetail.components.DetailHeader
import com.felipimatheuz.primehunt.ui.screen.primedetail.components.DetailQuickActions
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.viewmodel.primedetail.PrimeDetailIntent
import com.felipimatheuz.primehunt.ui.viewmodel.primedetail.PrimeDetailState
import com.felipimatheuz.primehunt.ui.viewmodel.primedetail.PrimeDetailViewModel

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

    PrimeDetailContent(
        padding = padding,
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack
    )
}
@Composable
fun PrimeDetailContent(
    padding: PaddingValues,
    state: PrimeDetailState,
    onIntent: (PrimeDetailIntent) -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        if (state.isLoading) {
            LoadingUI(
                loadText = stringResource(R.string.load_detail_content),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            state.primeSet?.let { set ->
                Column(modifier = Modifier.fillMaxSize()) {
                    DetailHeader(set)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {

                        item {
                            DetailQuickActions(
                                onAddSet = {
                                    onIntent(
                                        PrimeDetailIntent.UpdateSetQuantity(
                                            1
                                        )
                                    )
                                },
                                onRemoveSet = {
                                    onIntent(
                                        PrimeDetailIntent.UpdateSetQuantity(
                                            -1
                                        )
                                    )
                                }
                            )
                        }

                        set.parts.forEach { part ->
                            item(key = part.id) {
                                DetailComponentItem(part) { delta ->
                                    onIntent(
                                        PrimeDetailIntent.UpdateQuantity(
                                            part.id,
                                            delta
                                        )
                                    )
                                }
                            }

                            if (part.nestedParts.isNotEmpty()) {
                                items(
                                    part.nestedParts,
                                    key = { "nested_${part.id}_${it.id}" }) { nested ->
                                    DetailComponentItem(nested, isNested = true) { delta ->
                                        onIntent(
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

@Preview(showBackground = true)
@Composable
fun PrimeDetailScreenPreview() {
    PrimeTrackerTheme {
        PrimeDetailContent(
            padding = PaddingValues(),
            state = PrimeDetailState(),
            onIntent = {},
            onBack = {}
        )
    }
}