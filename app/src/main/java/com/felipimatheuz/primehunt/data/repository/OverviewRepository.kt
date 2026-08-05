package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.entity.GoalWithTag
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.data.remote.dao.ManifestDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeCollectionDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimePartDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeSetDao
import com.felipimatheuz.primehunt.data.remote.dao.RelicDao
import com.felipimatheuz.primehunt.data.remote.entity.LocalManifest
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.domain.repository.OverviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverviewRepositoryImpl @Inject constructor(
    private val primeCollectionDao: PrimeCollectionDao,
    private val primeSetDao: PrimeSetDao,
    private val primePartDao: PrimePartDao,
    private val relicDao: RelicDao,
    private val goalDao: GoalDao,
    private val manifestDao: ManifestDao
) : OverviewRepository {

    override fun getDatabaseSummary(): Flow<DatabaseSummary> = combine(
        primeCollectionDao.count(),
        primeSetDao.count(),
        primePartDao.count(),
        relicDao.count()
    ) { collections, sets, parts, relics ->
        DatabaseSummary(collections, sets, parts, relics)
    }

    override fun getRelicSummary(): Flow<RelicSummary> = combine(
        relicDao.countBySource(RelicSource.MISSION),
        relicDao.countBySource(RelicSource.VAULT),
        relicDao.countBySource(RelicSource.RESURGENCE),
        relicDao.countBySource(RelicSource.BARO)
    ) { mission, vault, resurgence, baro ->
        RelicSummary(mission, vault, resurgence, baro)
    }

    override fun getGoalSummary(): Flow<GoalSummary> = combine(
        goalDao.countByStatus(GoalStatus.ACTIVE),
        goalDao.countByStatus(GoalStatus.COMPLETED)
    ) { active, completed ->
        GoalSummary(active, completed)
    }

    override fun observeManifest(): Flow<LocalManifest?> = manifestDao.observeManifest()

    override fun observeGoalsWithTags(): Flow<List<GoalWithTag>> = goalDao.observeAllWithTags()
}

data class DatabaseSummary(
    val collections: Int,
    val sets: Int,
    val parts: Int,
    val relics: Int
)

data class RelicSummary(
    val available: Int,
    val vaulted: Int,
    val resurgence: Int,
    val baro: Int
)

data class GoalSummary(
    val active: Int,
    val completed: Int
)
