package com.felipimatheuz.primehunt.ui.screen.primeset

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.ui.screen.primeset.components.CategoryHeader
import com.felipimatheuz.primehunt.ui.screen.primeset.components.CollectionCard
import com.felipimatheuz.primehunt.ui.screen.primeset.components.EmptyStateMessage
import com.felipimatheuz.primehunt.ui.screen.primeset.components.FilterBottomSheet
import com.felipimatheuz.primehunt.ui.screen.primeset.components.PrimeSetCard
import com.felipimatheuz.primehunt.ui.screen.primeset.components.PrimeSetSkeleton
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.viewmodel.primeset.PrimeSetIntent
import com.felipimatheuz.primehunt.ui.viewmodel.primeset.PrimeSetState
import com.felipimatheuz.primehunt.ui.viewmodel.primeset.PrimeSetViewModel

@Composable
fun PrimeSetScreen(
    paddingValues: PaddingValues,
    viewModel: PrimeSetViewModel = hiltViewModel(),
    onSetClick: (PrimeSetDomain) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PrimeSetContent(
        paddingValues = paddingValues,
        state = state,
        onIntent = viewModel::onIntent,
        onSetClick = onSetClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimeSetContent(
    paddingValues: PaddingValues,
    state: PrimeSetState,
    onIntent: (PrimeSetIntent) -> Unit,
    onSetClick: (PrimeSetDomain) -> Unit
) {
    val viewOptions = remember {
        listOf(
            R.string.tab_collections,
            R.string.tab_prime_sets
        )
    }

    val sheetState = rememberModalBottomSheetState()
    var showFilterSheet by remember { mutableStateOf(false) }

    if (state.isLoading) {
        PrimeSetSkeleton(paddingValues = paddingValues)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = state.queryFilter,
                        onQueryChange = { onIntent(PrimeSetIntent.Search(it)) },
                        onSearch = { },
                        expanded = false,
                        onExpandedChange = { },
                        placeholder = { Text(stringResource(R.string.search_label)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (state.queryFilter.isNotEmpty()) {
                                    IconButton(onClick = { onIntent(PrimeSetIntent.ClearSearch) }) {
                                        Icon(
                                            painterResource(R.drawable.btn_close),
                                            contentDescription = stringResource(R.string.close)
                                        )
                                    }
                                }
                                BadgedBox(
                                    badge = {
                                        if (state.activeFilters.activeCount > 0) {
                                            Badge {
                                                Text(state.activeFilters.activeCount.toString())
                                            }
                                        }
                                    }
                                ) {
                                    IconButton(onClick = { showFilterSheet = true }) {
                                        Icon(
                                            painter = painterResource(R.drawable.btn_filter),
                                            contentDescription = stringResource(R.string.filter),
                                            tint = if (state.activeFilters.activeCount > 0) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                        )
                                    }
                                }
                            }
                        }
                    )
                },
                expanded = false,
                onExpandedChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) { }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                viewOptions.forEachIndexed { index, labelRes ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = viewOptions.size
                        ),
                        onClick = { onIntent(PrimeSetIntent.ChangeView(index)) },
                        selected = state.selectedView == index
                    ) {
                        Text(stringResource(labelRes))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AnimatedContent(
                    targetState = state.selectedView,
                    label = "viewTransition",
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    }
                ) { selectedView ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        if (selectedView == 0) {
                            if (state.collections.isEmpty()) {
                                item { EmptyStateMessage() }
                            } else {
                                items(state.collections, key = { it.id }) { collection ->
                                    CollectionCard(
                                        modifier = Modifier.animateItem(),
                                        collection = collection,
                                        onSetClick = onSetClick
                                    )
                                }
                            }
                        } else {
                            if (state.groupedSets.isEmpty()) {
                                item { EmptyStateMessage() }
                            } else {
                                state.groupedSets.forEach { (type, sets) ->
                                    item(key = type.name) {
                                        CategoryHeader(
                                            modifier = Modifier.animateItem(),
                                            titleRes = type.displayNameRes
                                        )
                                    }
                                    items(sets, key = { it.id }) { set ->
                                        PrimeSetCard(
                                            primeSet = set,
                                            modifier = Modifier
                                                .padding(vertical = 4.dp)
                                                .animateItem(),
                                            onClick = { onSetClick(set) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            filters = state.activeFilters,
            onFiltersChanged = { onIntent(PrimeSetIntent.UpdateFilters(it)) },
            onDismiss = { showFilterSheet = false },
            sheetState = sheetState
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrimeSetScreenPreview() {
    PrimeTrackerTheme {
        PrimeSetContent(
            paddingValues = PaddingValues(),
            state = PrimeSetState(),
            onIntent = {},
            onSetClick = {}
        )
    }
}
