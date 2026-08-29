package com.felipimatheuz.primehunt.ui.viewmodel.goals

import com.felipimatheuz.primehunt.ui.mvi.MviIntent

sealed interface GoalsIntent : MviIntent {
    data class Search(val query: String) : GoalsIntent
    data object ClearSearch : GoalsIntent
    data class UpdateFilters(val filters: GoalsFilters) : GoalsIntent
}
