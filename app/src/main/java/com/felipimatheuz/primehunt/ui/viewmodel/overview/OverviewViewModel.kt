package com.felipimatheuz.primehunt.ui.viewmodel.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.data.repository.DatabaseSummary
import com.felipimatheuz.primehunt.data.repository.OverviewRepository
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.domain.usecase.primeset.GetPrimeSetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    getPrimeSetsUseCase: GetPrimeSetsUseCase,
    overviewRepository: OverviewRepository
) : ViewModel() {

    private val setsFlow = getPrimeSetsUseCase.observeAllSets().distinctUntilChanged()

    private val primeSetsUiFlow = setsFlow
        .map { calculatePrimeSetsUi(it) }
        .distinctUntilChanged()

    private val tradeUiFlow = setsFlow
        .map { calculateTradeUi(it) }
        .distinctUntilChanged()

    private val databaseUiFlow = combine(
        overviewRepository.getDatabaseSummary(),
        overviewRepository.observeManifest()
    ) { summary, manifest ->
        calculateDatabaseUi(summary, manifest?.lastSync)
    }.distinctUntilChanged()

    private val relicsUiFlow = overviewRepository.getRelicSummary()
        .map { calculateRelicsUi(it) }
        .distinctUntilChanged()

    private val goalsUiFlow = combine(
        overviewRepository.getGoalSummary(),
        overviewRepository.observeGoalsWithTags()
    ) { summary, goalsWithTags ->
        calculateGoalsUi(summary, goalsWithTags)
    }.distinctUntilChanged()

    val uiState: StateFlow<OverviewState> = combine(
        primeSetsUiFlow,
        databaseUiFlow,
        relicsUiFlow,
        goalsUiFlow,
        tradeUiFlow
    ) { primeSets, database, relics, goals, trade ->
        OverviewState(primeSets, database, relics, goals, trade)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OverviewState()
    )

    private fun calculatePrimeSetsUi(sets: List<PrimeSetDomain>): PrimeSetsOverviewUi {
        if (sets.isEmpty()) return PrimeSetsOverviewUi()

        var completedCount = 0
        var inProgressCount = 0
        var missingCount = 0

        sets.forEach {
            when {
                it.isComplete -> completedCount++
                it.isNotStarted -> missingCount++
                else -> inProgressCount++
            }
        }

        val progress = completedCount.toFloat() / sets.size

        val categories = sets.groupBy {
            when (it.type) {
                PrimeType.ARCHWING, PrimeType.COMPANION, PrimeType.ARCH_GUN -> PrimeType.COMPANION
                else -> it.type
            }
        }.map { (type, typeSets) ->
            val typeCompleted = typeSets.count { it.isComplete }
            CategoryOverviewUi(
                nameRes = if (type == PrimeType.COMPANION) R.string.overview_others else type.displayNameRes,
                percentage = (typeCompleted * 100) / typeSets.size
            )
        }.sortedByDescending { it.percentage }

        return PrimeSetsOverviewUi(
            progress = progress,
            completedSets = completedCount,
            inProgressSets = inProgressCount,
            missingSets = missingCount,
            categories = categories
        )
    }

    private fun calculateDatabaseUi(
        db: DatabaseSummary,
        lastSync: Long?
    ): DatabaseOverviewUi {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val lastSyncStr = lastSync?.let { dateFormat.format(Date(it)) } ?: "—"
        return DatabaseOverviewUi(
            collectionsCount = db.collections,
            setsCount = db.sets,
            partsCount = db.parts,
            relicsCount = db.relics,
            lastSync = lastSyncStr
        )
    }

    private fun calculateRelicsUi(relic: com.felipimatheuz.primehunt.data.repository.RelicSummary): RelicsOverviewUi {
        return RelicsOverviewUi(
            available = relic.available,
            vaulted = relic.vaulted,
            resurgence = relic.resurgence,
            baro = relic.baro
        )
    }

    private fun calculateGoalsUi(
        goal: com.felipimatheuz.primehunt.data.repository.GoalSummary,
        goalsWithTags: List<com.felipimatheuz.primehunt.data.local.entity.GoalWithTag>
    ): GoalsOverviewUi {
        val activeGoals = goalsWithTags.filter { it.goal.status == GoalStatus.ACTIVE }
        val mainTag = activeGoals
            .map { it.tag.name }
            .groupBy { it }
            .maxByOrNull { it.value.size }
            ?.key ?: "—"

        return GoalsOverviewUi(
            activeGoals = goal.active,
            completedGoals = goal.completed,
            mainTag = mainTag
        )
    }

    private fun calculateTradeUi(sets: List<PrimeSetDomain>): TradeOverviewUi {
        var duplicateSets = 0
        var duplicateParts = 0

        sets.forEach { set ->
            duplicateParts += set.parts.sumOf { part ->
                maxOf(0, part.ownedQuantity - part.neededQuantity)
            }

            val possibleExtraSets = if (set.parts.isNotEmpty()) {
                set.parts.minOf { part ->
                    if (part.neededQuantity > 0) {
                        (part.ownedQuantity - part.neededQuantity) / part.neededQuantity
                    } else Int.MAX_VALUE
                }
            } else 0

            duplicateSets += maxOf(0, if (possibleExtraSets == Int.MAX_VALUE) 0 else possibleExtraSets)
        }

        return TradeOverviewUi(
            duplicateSets = duplicateSets,
            duplicateParts = duplicateParts
        )
    }
}
