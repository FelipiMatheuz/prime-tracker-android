package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.remote.dao.PrimeCollectionDao
import com.felipimatheuz.primehunt.data.remote.dao.PrimeCollectionSetDao
import com.felipimatheuz.primehunt.domain.model.PrimeCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrimeSetRepository @Inject constructor(
    private val dataStore: PrimeDataStore,
    private val collectionDao: PrimeCollectionDao,
    private val collectionSetDao: PrimeCollectionSetDao
) {

    fun observeCollections(): Flow<List<PrimeCollection>> = combine(
        collectionDao.getAll().distinctUntilChanged(),
        collectionSetDao.getAll().distinctUntilChanged(),
        dataStore.allSets
    ) { collections, relations, allSets ->
        collections.map { coll ->
            val setIds = relations.filter { it.collectionId == coll.id }.map { it.primeSetId }
            PrimeCollection(
                id = coll.id,
                name = coll.name,
                promoImage = coll.promoImage,
                sets = allSets.filter { it.id in setIds }
            )
        }
    }

    fun observeWithoutCollection(): Flow<PrimeCollection> = combine(
        collectionSetDao.getAll().distinctUntilChanged(),
        dataStore.allSets
    ) { relations, allSets ->
        val setsWithCollection = relations.map { it.primeSetId }.toSet()
        val setsWithout = allSets.filter { it.id !in setsWithCollection }
        PrimeCollection(
            "none",
            "",
            "https://www-static.warframe.com/images/guide/quests/sacrifice-key.jpg",
            setsWithout
        )
    }
}
