package com.felipimatheuz.primehunt.ui.screen.relic

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.RelicDomain
import com.felipimatheuz.primehunt.ui.screen.components.PrimeSearchBar
import com.felipimatheuz.primehunt.ui.screen.relic.components.*
import com.felipimatheuz.primehunt.ui.viewmodel.relic.RelicIntent
import com.felipimatheuz.primehunt.ui.viewmodel.relic.RelicState
import com.felipimatheuz.primehunt.ui.viewmodel.relic.RelicViewModel

@Composable
fun RelicsScreen(
    paddingValues: PaddingValues,
    viewModel: RelicViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RelicsContent(
        paddingValues = paddingValues,
        state = state,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelicsContent(
    paddingValues: PaddingValues,
    state: RelicState,
    onIntent: (RelicIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedRelic by remember { mutableStateOf<RelicDomain?>(null) }

    var localSearchQuery by rememberSaveable { mutableStateOf(state.queryFilter) }

    LaunchedEffect(state.queryFilter) {
        if (localSearchQuery != state.queryFilter) {
            localSearchQuery = state.queryFilter
        }
    }

    if (state.isLoading) {
        RelicSkeleton(paddingValues = paddingValues)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimeSearchBar(
                query = localSearchQuery,
                onQueryChange = {
                    localSearchQuery = it
                    onIntent(RelicIntent.Search(it))
                },
                placeholderRes = R.string.relic_search_label,
                activeFiltersCount = state.activeFilters.activeCount,
                onFilterClick = { showFilterSheet = true },
                onClearClick = { onIntent(RelicIntent.ClearSearch) }
            )

            RelicsViewSelector(
                selectedView = state.selectedView,
                onViewChange = { onIntent(RelicIntent.ChangeView(it)) }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AnimatedContent(
                    targetState = state.selectedView,
                    label = "view_transition",
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                            animationSpec = tween(
                                300
                            )
                        )
                    }
                ) { targetView ->
                    val grouped = remember(targetView, state.groupedRelics) { state.groupedRelics }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        grouped.forEach { (group, relics) ->
                            item(key = group.toString()) {
                                RelicSection(
                                    title = stringResource(group.titleRes),
                                    relics = relics,
                                    onRelicClick = { selectedRelic = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        RelicFilterBottomSheet(
            currentView = state.selectedView,
            filters = state.activeFilters,
            onFiltersChanged = { onIntent(RelicIntent.UpdateFilters(it)) },
            onDismiss = { showFilterSheet = false },
            sheetState = sheetState
        )
    }

    selectedRelic?.let { relic ->
        RelicDetailsDialog(
            relic = relic,
            onDismiss = { selectedRelic = null }
        )
    }
}
