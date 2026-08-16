package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.local.dao.*
import com.felipimatheuz.primehunt.data.model.PrimeBaseData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrimeDataStore @Inject constructor(
    setDao: PrimeSetDao,
    partDao: PrimePartDao,
    componentDao: PrimeComponentDao,
    relicDao: RelicDao,
    val inventoryDao: InventoryDao,
    val goalDao: GoalDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val sets = setDao.getAll().distinctUntilChanged()
    val parts = partDao.getAll().distinctUntilChanged()
    val components = componentDao.getAll().distinctUntilChanged()
    val relics = relicDao.getAll().distinctUntilChanged()

    val baseData: Flow<PrimeBaseData> = combine(
        sets,
        parts,
        components,
        relics
    ) { sets, parts, components, relics ->
        PrimeBaseData(sets, parts, components, relics)
    }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PrimeBaseData(emptyList(), emptyList(), emptyList(), emptyList())
        )
}
