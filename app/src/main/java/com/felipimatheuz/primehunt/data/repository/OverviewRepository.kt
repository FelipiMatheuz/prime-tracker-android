package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.domain.model.ManifestDomain
import com.felipimatheuz.primehunt.domain.model.enums.GoalStatus
import com.felipimatheuz.primehunt.data.local.dao.ManifestDao
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverviewRepositoryImpl @Inject constructor(
    private val primeRepository: PrimeRepository,
    private val goalRepository: GoalRepository,
    private val manifestDao: ManifestDao
) : OverviewRepository {

    override fun getDatabaseSummary(): Flow<DatabaseSummary> = primeRepository.getDatabaseCounts().map {
        DatabaseSummary(it.collections, it.sets, it.parts, it.relics)
    }

    override fun getRelicSummary(): Flow<RelicSummary> = primeRepository.getRelicCounts().map {
        RelicSummary(it.available, it.vaulted, it.resurgence, it.baro)
    }

    override fun getGoalSummary(): Flow<GoalSummary> = combine(
        goalRepository.countByStatus(GoalStatus.ACTIVE),
        goalRepository.countByStatus(GoalStatus.COMPLETED)
    ) { active, completed ->
        GoalSummary(active, completed)
    }

    override fun observeManifest(): Flow<ManifestDomain?> = manifestDao.observeManifest().map { 
        it?.let { 
            ManifestDomain(
                lastSync = it.lastSync,
                collectionsHash = it.collectionsHash,
                primeSetsHash = it.primeSetsHash,
                relicsHash = it.relicsHash,
                isRelicsValid = it.isRelicsValid,
                isSetsValid = it.isSetsValid,
                isCollectionsValid = it.isCollectionsValid
            )
        }
    }

    override fun observeGoalsWithTags(): Flow<List<GoalDomain>> = goalRepository.observeAllWithTags()
}
