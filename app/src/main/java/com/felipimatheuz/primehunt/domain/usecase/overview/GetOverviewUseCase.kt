package com.felipimatheuz.primehunt.domain.usecase.overview

import com.felipimatheuz.primehunt.domain.model.enums.GoalStatus
import com.felipimatheuz.primehunt.domain.model.enums.PrimeType
import com.felipimatheuz.primehunt.domain.model.*
import com.felipimatheuz.primehunt.domain.repository.*
import com.felipimatheuz.primehunt.domain.usecase.primeset.GetPrimeSetsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetOverviewUseCase @Inject constructor(
    private val getPrimeSetsUseCase: GetPrimeSetsUseCase,
    private val overviewRepository: OverviewRepository
) {

    operator fun invoke(): Flow<OverviewDomainModel> {
        val setsFlow = getPrimeSetsUseCase.observeAllSets().distinctUntilChanged()

        val primeSetsDomainFlow = setsFlow
            .map { calculatePrimeSetsDomain(it) }
            .distinctUntilChanged()

        val tradeDomainFlow = setsFlow
            .map { calculateTradeDomain(it) }
            .distinctUntilChanged()

        val databaseDomainFlow = combine(
            overviewRepository.getDatabaseSummary(),
            overviewRepository.observeManifest()
        ) { summary, manifest ->
            calculateDatabaseDomain(summary, manifest)
        }.distinctUntilChanged()

        val relicsDomainFlow = overviewRepository.getRelicSummary()
            .map { calculateRelicsDomain(it) }
            .distinctUntilChanged()

        val goalsDomainFlow = combine(
            overviewRepository.getGoalSummary(),
            overviewRepository.observeGoalsWithTags()
        ) { summary, goalsWithTags ->
            calculateGoalsDomain(summary, goalsWithTags)
        }.distinctUntilChanged()

        return combine(
            primeSetsDomainFlow,
            databaseDomainFlow,
            relicsDomainFlow,
            goalsDomainFlow,
            tradeDomainFlow
        ) { primeSets, database, relics, goals, trade ->
            OverviewDomainModel(primeSets, database, relics, goals, trade)
        }.flowOn(kotlinx.coroutines.Dispatchers.Default)
    }

    private fun calculatePrimeSetsDomain(sets: List<PrimeSetDomain>): PrimeSetsOverviewDomain {
        if (sets.isEmpty()) return PrimeSetsOverviewDomain()

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
            CategoryOverviewDomain(
                type = type,
                percentage = (typeCompleted * 100) / typeSets.size
            )
        }.sortedByDescending { it.percentage }

        return PrimeSetsOverviewDomain(
            progress = progress,
            completedSets = completedCount,
            inProgressSets = inProgressCount,
            missingSets = missingCount,
            categories = categories
        )
    }

    private fun calculateDatabaseDomain(
        db: DatabaseSummary,
        manifest: ManifestDomain?
    ): DatabaseOverviewDomain {
        return DatabaseOverviewDomain(
            collectionsCount = db.collections,
            setsCount = db.sets,
            partsCount = db.parts,
            relicsCount = db.relics,
            lastSyncTimestamp = manifest?.lastSync,
            isRelicsValid = manifest?.isRelicsValid ?: true,
            isSetsValid = manifest?.isSetsValid ?: true,
            isCollectionsValid = manifest?.isCollectionsValid ?: true
        )
    }

    private fun calculateRelicsDomain(relic: RelicSummary): RelicsOverviewDomain {
        return RelicsOverviewDomain(
            available = relic.available,
            vaulted = relic.vaulted,
            resurgence = relic.resurgence,
            baro = relic.baro
        )
    }

    private fun calculateGoalsDomain(
        goal: GoalSummary,
        goalsWithTags: List<GoalDomain>
    ): GoalsOverviewDomain {
        val activeGoals = goalsWithTags.filter { it.status == GoalStatus.ACTIVE }
        val mainTag = activeGoals
            .groupBy { it.tag }
            .maxByOrNull { it.value.size }
            ?.key

        return GoalsOverviewDomain(
            activeGoals = goal.active,
            completedGoals = goal.completed,
            mainTag = mainTag
        )
    }

    private fun calculateTradeDomain(sets: List<PrimeSetDomain>): TradeOverviewDomain {
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

        return TradeOverviewDomain(
            duplicateSets = duplicateSets,
            duplicateParts = duplicateParts,
            averageDucatPrice = (duplicateSets + duplicateParts) * AVERAGE_DUCAT_VALUE
        )
    }

    companion object {
        private const val AVERAGE_DUCAT_VALUE = 32
    }
}
