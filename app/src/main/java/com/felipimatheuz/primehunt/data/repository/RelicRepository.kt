package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.domain.model.RelicDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RelicRepository @Inject constructor(
    private val dataStore: PrimeDataStore
) {
    fun observeRelics(): Flow<List<RelicDomain>> = dataStore.allRelics
}
