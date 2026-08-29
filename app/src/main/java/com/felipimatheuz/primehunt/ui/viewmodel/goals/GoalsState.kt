package com.felipimatheuz.primehunt.ui.viewmodel.goals

import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.ui.mvi.MviState

data class GoalsState(
    val goals: List<GoalDomain> = emptyList(),
    val availableTags: List<GoalTagDomain> = emptyList(),
    val queryFilter: String = "",
    val activeFilters: GoalsFilters = GoalsFilters(),
    val isLoading: Boolean = true
) : MviState
