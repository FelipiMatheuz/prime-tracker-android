package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.GoalDao
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.data.remote.dao.ManifestDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeCollectionDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimePartDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeSetDao
import com.felipimatheuz.primehunt.data.remote.dao.RelicDao
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverviewRepository @Inject constructor(
    private val primeCollectionDao: PrimeCollectionDao,
    private val primeSetDao: PrimeSetDao,
    private val primePartDao: PrimePartDao,
    private val relicDao: RelicDao,
    private val goalDao: GoalDao,
    private val manifestDao: ManifestDao
) {

    fun getDatabaseSummary() = combine(
        primeCollectionDao.count(),
        primeSetDao.count(),
        primePartDao.count(),
        relicDao.count()
    ) { collections, sets, parts, relics ->
        DatabaseSummary(collections, sets, parts, relics)
    }

    fun getRelicSummary() = combine(
        relicDao.countBySource(RelicSource.MISSION),
        relicDao.countBySource(RelicSource.VAULT),
        relicDao.countBySource(RelicSource.RESURGENCE),
        relicDao.countBySource(RelicSource.BARO)
    ) { mission, vault, resurgence, baro ->
        RelicSummary(mission, vault, resurgence, baro)
    }

    fun getGoalSummary() = combine(
        goalDao.countByStatus(GoalStatus.ACTIVE),
        goalDao.countByStatus(GoalStatus.COMPLETED)
    ) { active, completed ->
        GoalSummary(active, completed)
    }

    fun observeManifest() = manifestDao.observeManifest()

    fun observeGoalsWithTags() = goalDao.observeAllWithTags()
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
