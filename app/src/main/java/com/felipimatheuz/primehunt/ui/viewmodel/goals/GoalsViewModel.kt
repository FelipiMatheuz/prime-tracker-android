package com.felipimatheuz.primehunt.ui.viewmodel.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.data.local.dao.GoalTagDao
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.usecase.goal.GetGoalsUseCase
import com.felipimatheuz.primehunt.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class GoalsViewModel @Inject constructor(
    getGoalsUseCase: GetGoalsUseCase,
    tagDao: GoalTagDao
) : ViewModel(), MviViewModel<GoalsState, GoalsIntent> {

    private val _searchText = MutableStateFlow("")
    private val _filters = MutableStateFlow(GoalsFilters())

    init {
        tagDao.observeAll()
            .distinctUntilChanged()
            .onEach { tags ->
                val tagIds = tags.map { it.id }.toSet()
                _filters.value = _filters.value.copy(
                    allCategoryIds = tagIds,
                    categoryIds = _filters.value.categoryIds.ifEmpty { tagIds }
                )
            }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    @OptIn(FlowPreview::class)
    override val state: StateFlow<GoalsState> = combine(
        getGoalsUseCase().distinctUntilChanged(),
        tagDao.observeAll().distinctUntilChanged(),
        _searchText.debounce(300.milliseconds).distinctUntilChanged(),
        _filters
    ) { goals, tags, query, filters ->
        val filtered = applyFilters(goals, query, filters)
            .sortedWith(compareBy({ it.tag.name }, { it.targetName }))

        GoalsState(
            goals = filtered,
            availableTags = tags,
            queryFilter = query,
            activeFilters = filters,
            isLoading = false
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = GoalsState()
        )

    private fun applyFilters(
        goals: List<GoalDomain>,
        query: String,
        filters: GoalsFilters
    ): List<GoalDomain> {
        return goals.filter { goal ->
            val matchesQuery = query.isEmpty() ||
                    goal.targetName.contains(query, ignoreCase = true) ||
                    (goal.note?.contains(query, ignoreCase = true) == true) ||
                    goal.tag.name.contains(query, ignoreCase = true)

            val matchesStatus = goal.status == filters.status
            val matchesType = filters.targetTypes.contains(goal.targetType)
            val matchesCategory = filters.categoryIds.isEmpty() || filters.categoryIds.contains(goal.tag.id)

            matchesQuery && matchesStatus && matchesType && matchesCategory
        }
    }

    override fun onIntent(intent: GoalsIntent) {
        when (intent) {
            is GoalsIntent.Search -> _searchText.value = intent.query
            is GoalsIntent.ClearSearch -> _searchText.value = ""
            is GoalsIntent.UpdateFilters -> _filters.value = intent.filters
        }
    }
}
