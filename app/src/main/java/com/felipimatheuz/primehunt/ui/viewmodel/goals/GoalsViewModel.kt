package com.felipimatheuz.primehunt.ui.viewmodel.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.domain.model.prefs.GoalUiPrefs
import com.felipimatheuz.primehunt.domain.repository.GoalRepository
import com.felipimatheuz.primehunt.domain.repository.UiPreferencesRepository
import com.felipimatheuz.primehunt.domain.usecase.goal.GetGoalsUseCase
import com.felipimatheuz.primehunt.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val repository: GoalRepository,
    private val getGoalsUseCase: GetGoalsUseCase,
    private val uiPreferencesRepository: UiPreferencesRepository
) : ViewModel(), MviViewModel<GoalsState, GoalsIntent> {

    private val _state = MutableStateFlow(GoalsState())
    override val state: StateFlow<GoalsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = uiPreferencesRepository.goalPrefs.take(1).first()
            _state.update { 
                it.copy(
                    activeFilters = it.activeFilters.copy(
                        status = prefs.status,
                        targetTypes = prefs.targetTypes,
                        categoryIds = prefs.categoryIds
                    )
                ) 
            }
            observeGoals()
            observeTags()
        }
    }

    private fun observeGoals() {
        combine(
            getGoalsUseCase(),
            _state.map { it.activeFilters }.distinctUntilChanged(),
            _state.map { it.queryFilter }.distinctUntilChanged()
        ) { goals, filters, query ->
            val filtered = goals.filter { goal ->
                val matchesStatus = goal.status == filters.status
                val matchesType = filters.targetTypes.contains(goal.targetType)
                val matchesQuery = query.isBlank() || goal.targetName.contains(query, ignoreCase = true)
                val matchesCategory = filters.categoryIds.isEmpty() || filters.categoryIds.contains(goal.tag.id)
                
                matchesStatus && matchesType && matchesQuery && matchesCategory
            }
            
            _state.update { it.copy(goals = filtered, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    private fun observeTags() {
        repository.observeAllTags()
            .onEach { tags ->
                _state.update { 
                    it.copy(
                        availableTags = tags,
                        activeFilters = it.activeFilters.copy(allCategoryIds = tags.map { t -> t.id }.toSet())
                    ) 
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: GoalsIntent) {
        when (intent) {
            is GoalsIntent.Search -> _state.update { it.copy(queryFilter = intent.query) }
            is GoalsIntent.UpdateFilters -> {
                _state.update { it.copy(activeFilters = intent.filters) }
                savePrefs(intent.filters)
            }
            GoalsIntent.ClearSearch -> _state.update { it.copy(queryFilter = "") }
        }
    }

    private fun savePrefs(filters: GoalsFilters) {
        viewModelScope.launch {
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
