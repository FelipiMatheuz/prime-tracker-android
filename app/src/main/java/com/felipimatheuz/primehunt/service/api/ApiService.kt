package com.felipimatheuz.primehunt.service.api

import com.felipimatheuz.primehunt.model.PrimeItem
import com.felipimatheuz.primehunt.model.PrimeSet
import com.felipimatheuz.primehunt.model.RelicSet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor(private val trackerService: PrimeTrackerService) {

    suspend fun getPrimeSetList(): List<PrimeSet> = trackerService.primeSets()

    suspend fun getOtherPrimeList(): List<PrimeItem> = trackerService.otherPrimes()

    suspend fun getRelicSetList(): List<RelicSet> = trackerService.relics()
        .filter { relicSet -> relicSet.name.contains("Intact") && relicSet.rewards.isNotEmpty() }
}