package com.felipimatheuz.primehunt.ui.viewmodel.overview

import androidx.compose.ui.graphics.Color
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain

data class OverviewState(
    val primeSets: PrimeSetsOverviewUi = PrimeSetsOverviewUi(),
    val database: DatabaseOverviewUi = DatabaseOverviewUi(),
    val relics: RelicsOverviewUi = RelicsOverviewUi(),
    val goals: GoalsOverviewUi = GoalsOverviewUi(),
    val trade: TradeOverviewUi = TradeOverviewUi()
)

data class PrimeSetsOverviewUi(
    val progress: Float = 0f,
    val completedSets: Int = 0,
    val inProgressSets: Int = 0,
    val missingSets: Int = 0,
    val categories: List<CategoryOverviewUi> = emptyList()
)

data class CategoryOverviewUi(
    val nameRes: Int,
    val percentage: Int
)

data class DatabaseOverviewUi(
    val collectionsCount: Int = 0,
    val setsCount: Int = 0,
    val partsCount: Int = 0,
    val relicsCount: Int = 0,
    val lastSync: String = "—",
    val collectionsStatusColor: Color = Color.Unspecified,
    val setsStatusColor: Color = Color.Unspecified,
    val relicsStatusColor: Color = Color.Unspecified
)

data class RelicsOverviewUi(
    val available: Int = 0,
    val vaulted: Int = 0,
    val resurgence: Int = 0,
    val baro: Int = 0
)

data class GoalsOverviewUi(
    val activeGoals: Int = 0,
    val completedGoals: Int = 0,
    val mainTag: GoalTagDomain? = null
)

data class TradeOverviewUi(
    val duplicateSets: Int = 0,
    val duplicateParts: Int = 0,
    val averageDucatPrice: Int = 0
)
