package com.felipimatheuz.primehunt.ui.screen.goals

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.enums.GoalIcons
import com.felipimatheuz.primehunt.domain.model.enums.GoalStatus
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.screen.components.PrimeSearchBar
import com.felipimatheuz.primehunt.ui.screen.goals.components.GoalCard
import com.felipimatheuz.primehunt.ui.screen.goals.components.GoalEmptyState
import com.felipimatheuz.primehunt.ui.screen.goals.components.GoalSkeleton
import com.felipimatheuz.primehunt.ui.screen.goals.components.GoalsFilterBottomSheet
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.viewmodel.goals.GoalsIntent
import com.felipimatheuz.primehunt.ui.viewmodel.goals.GoalsState
import com.felipimatheuz.primehunt.ui.viewmodel.goals.GoalsViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun GoalsScreen(
    paddingValues: PaddingValues,
    viewModel: GoalsViewModel = hiltViewModel(),
    onAddGoal: () -> Unit,
    onGoalClick: (GoalDomain) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    GoalsContent(
        paddingValues = paddingValues,
        state = state,
        onIntent = viewModel::onIntent,
        onAddGoal = onAddGoal,
        onGoalClick = onGoalClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsContent(
    paddingValues: PaddingValues,
    state: GoalsState,
    onIntent: (GoalsIntent) -> Unit,
    onAddGoal: () -> Unit,
    onGoalClick: (GoalDomain) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFilterSheet by remember { mutableStateOf(false) }

    var localSearchQuery by rememberSaveable { mutableStateOf(state.queryFilter) }

    val gridState = rememberLazyGridState()
    var isInitialized by rememberSaveable { mutableStateOf(false) }
    var previousGoalIds by rememberSaveable { mutableStateOf(state.goals.map { it.id }) }
    var newGoalId by rememberSaveable { mutableStateOf<Long?>(null) }
    val recentlyCompletedId by rememberSaveable { mutableStateOf<Long?>(null) }

    val newlyAddedGoalId by remember(state.goals) {
        derivedStateOf {
            val currentIds = state.goals.map { it.id }
            if (!isInitialized) {
                null
            } else {
                (currentIds.toSet() - previousGoalIds.toSet()).firstOrNull()
            }
        }
    }

    LaunchedEffect(state.goals) {
        if (!isInitialized) {
            if (state.goals.isNotEmpty() || !state.isLoading) {
                previousGoalIds = state.goals.map { it.id }
                isInitialized = true
            }
            return@LaunchedEffect
        }

        newlyAddedGoalId?.let { id ->
            newGoalId = id
            val index = state.goals.indexOfFirst { it.id == id }
            if (index != -1) {
                gridState.animateScrollToItem(index)
            }
        }

        previousGoalIds = state.goals.map { it.id }
    }

    LaunchedEffect(newGoalId) {
        if (newGoalId != null) {
            delay(2.seconds)
            newGoalId = null
        }
    }

    LaunchedEffect(state.queryFilter) {
        if (localSearchQuery != state.queryFilter) {
            localSearchQuery = state.queryFilter
        }
    }

    if (state.isLoading) {
        GoalSkeleton(paddingValues = paddingValues)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            )
            {
                PrimeSearchBar(
                    query = localSearchQuery,
                    onQueryChange = {
                        localSearchQuery = it
                        onIntent(GoalsIntent.Search(it))
                    },
                    placeholderRes = R.string.goals_search_label,
                    activeFiltersCount = state.activeFilters.activeCount,
                    onFilterClick = { showFilterSheet = true },
                    onClearClick = { onIntent(GoalsIntent.ClearSearch) }
                )

                if (state.goals.isEmpty()) {
                    val isCompletedFiltered = state.activeFilters.status == GoalStatus.COMPLETED
                    GoalEmptyState(
                        title = stringResource(
                            if (isCompletedFiltered) R.string.empty_goals_completed_title
                            else R.string.empty_goals_active_title
                        ),
                        description = stringResource(
                            if (isCompletedFiltered) R.string.empty_goals_completed_desc
                            else R.string.empty_goals_active_desc
                        )
                    )
                } else {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Adaptive(minSize = 120.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.goals, key = { it.id }) { goal ->
                            GoalCard(
                                modifier = Modifier.animateItem(),
                                goal = goal,
                                isNew = goal.id == newGoalId,
                                isSuccess = goal.id == recentlyCompletedId,
                                onClick = { onGoalClick(goal) }
                            )
                        }
                    }
                }
            }

            val fabInteraction = remember { MutableInteractionSource() }
            FloatingActionButton(
                onClick = onAddGoal,
                interactionSource = fabInteraction,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .pressScale(
                        interactionSource = fabInteraction,
                        intensity = PressIntensity.VERY_SUBTLE
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    if (showFilterSheet) {
        GoalsFilterBottomSheet(
            filters = state.activeFilters,
            availableTags = state.availableTags,
            onFiltersChanged = { onIntent(GoalsIntent.UpdateFilters(it)) },
            onDismiss = { showFilterSheet = false },
            sheetState = sheetState
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun GoalsScreenPreview() {
    PrimeTrackerTheme {
        val mockGoals = listOf(
            GoalDomain(
                id = 1,
                targetId = "excalibur_prime",
                targetName = "Excalibur Prime",
                targetType = GoalTargetType.PRIME_SET,
                currentQuantity = 0,
                desiredQuantity = 1,
                status = GoalStatus.ACTIVE,
                note = "Main priority",
                tag = GoalTagDomain(1, "Warframe", GoalIcons.SLASH, 0xFF673AB7.toInt())
            ),
            GoalDomain(
                id = 2,
                targetId = "braton_prime_receiver",
                targetName = "Braton Prime Receiver",
                targetType = GoalTargetType.PRIME_PART,
                currentQuantity = 1,
                desiredQuantity = 2,
                status = GoalStatus.ACTIVE,
                note = null,
                tag = GoalTagDomain(2, "Primary", GoalIcons.VIRAL, 0xFF2196F3.toInt())
            )
        )
        GoalsContent(
            paddingValues = PaddingValues(),
            state = GoalsState(goals = mockGoals, isLoading = false),
            onIntent = {},
            onAddGoal = {},
            onGoalClick = {}
        )
    }
}
