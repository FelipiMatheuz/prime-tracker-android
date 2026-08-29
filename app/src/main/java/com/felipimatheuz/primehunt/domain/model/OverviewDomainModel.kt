package com.felipimatheuz.primehunt.domain.model

import androidx.compose.runtime.Immutable
import com.felipimatheuz.primehunt.domain.model.enums.PrimeType

@Immutable
data class OverviewDomainModel(
    val primeSets: PrimeSetsOverviewDomain = PrimeSetsOverviewDomain(),
    val database: DatabaseOverviewDomain = DatabaseOverviewDomain(),
    val relics: RelicsOverviewDomain = RelicsOverviewDomain(),
    val goals: GoalsOverviewDomain = GoalsOverviewDomain(),
    val trade: TradeOverviewDomain = TradeOverviewDomain()
)

@Immutable
data class PrimeSetsOverviewDomain(
    val progress: Float = 0f,
    val completedSets: Int = 0,
    val inProgressSets: Int = 0,
    val missingSets: Int = 0,
    val categories: List<CategoryOverviewDomain> = emptyList()
)

@Immutable
data class CategoryOverviewDomain(
    val type: PrimeType,
    val percentage: Int
)

@Immutable
data class DatabaseOverviewDomain(
    val collectionsCount: Int = 0,
    val setsCount: Int = 0,
    val partsCount: Int = 0,
    val relicsCount: Int = 0,
    val lastSyncTimestamp: Long? = null,
    val isRelicsValid: Boolean = true,
    val isSetsValid: Boolean = true,
    val isCollectionsValid: Boolean = true
)

@Immutable
data class RelicsOverviewDomain(
    val available: Int = 0,
    val vaulted: Int = 0,
    val resurgence: Int = 0,
    val baro: Int = 0
)

@Immutable
data class GoalsOverviewDomain(
    val activeGoals: Int = 0,
    val completedGoals: Int = 0,
    val mainTag: GoalTagDomain? = null
)

@Immutable
data class TradeOverviewDomain(
    val duplicateSets: Int = 0,
    val duplicateParts: Int = 0,
    val averageDucatPrice: Int = 0
)
