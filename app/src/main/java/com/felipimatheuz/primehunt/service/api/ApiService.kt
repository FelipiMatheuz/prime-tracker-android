package com.felipimatheuz.primehunt.service.api

import com.felipimatheuz.primehunt.model.PrimeItem
import com.felipimatheuz.primehunt.model.PrimeSet
import com.felipimatheuz.primehunt.model.RelicSet

class ApiService {

    companion object {
        suspend fun getPrimeSetList(): List<PrimeSet> = RetrofitConfig().trackerService().primeSets()

        suspend fun getOtherPrimeList(): List<PrimeItem> = RetrofitConfig().trackerService().otherPrimes()

        suspend fun getRelicSetList(): List<RelicSet> = RetrofitConfig().trackerService().relics()
            .filter { relicSet -> relicSet.name.contains("Intact") && relicSet.rewards.isNotEmpty() }
    }
}