package com.felipimatheuz.primehunt.ui.viewmodel.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.data.local.dao.GoalTagDao
import com.felipimatheuz.primehunt.data.local.preferences.GoalUiPrefs
import com.felipimatheuz.primehunt.data.repository.UiPreferencesRepository
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class GoalsViewModel @Inject constructor(
    getGoalsUseCase: GetGoalsUseCase,
    private val tagDao: GoalTagDao,
    private val uiPreferencesRepository: UiPreferencesRepository
) : ViewModel(), MviViewModel<GoalsState, GoalsIntent> {

    private val _searchText = MutableStateFlow("")
    private val _filters = MutableStateFlow(GoalsFilters())

    init {
        viewModelScope.launch {
            val prefs = uiPreferencesRepository.goalPrefs.take(1).first()
            _filters.update {
                it.copy(
                    status = prefs.status,
                    targetTypes = prefs.targetTypes,
                    categoryIds = prefs.categoryIds
                )
            }

            tagDao.observeAll()
                .distinctUntilChanged()
                .collect { tags ->
                    val tagIds = tags.map { it.id }.toSet()
                    _filters.update {
                        it.copy(
                            allCategoryIds = tagIds,
                            categoryIds = it.categoryIds.ifEmpty { tagIds }.intersect(tagIds)
                        )
                    }
                }
        }
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
            is GoalsIntent.UpdateFilters -> {
                _filters.value = intent.filters
                savePrefs()
            }
        }
    }

    private fun savePrefs() {
        viewModelScope.launch {
            val filters = _filters.value
            uiPreferencesRepository.updateGoalPrefs(
                GoalUiPrefs(
                    status = filters.status,
                    targetTypes = filters.targetTypes,
                    categoryIds = filters.categoryIds
                )
            )
        }
    }
}
