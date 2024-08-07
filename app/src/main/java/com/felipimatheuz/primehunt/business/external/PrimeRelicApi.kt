package com.felipimatheuz.primehunt.business.external

import androidx.annotation.Keep
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.felipimatheuz.primehunt.model.RelicSet
import com.felipimatheuz.primehunt.model.RelicTier
import com.felipimatheuz.primehunt.business.util.missingRewardList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.net.URL


class PrimeRelicApi {

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    private var relicData: List<RelicSet> = listOf()

    fun getRelicData(): List<RelicSet> {
        return relicData
    }

    suspend fun setRelicData() {
        return coroutineScope {
            val resultData = async(defaultDispatcher) { loadData() }
            relicData = resultData.await()
        }
    }

    @Keep
    private fun loadData(): List<RelicSet> {
        val origin = "https://raw.githubusercontent.com"
        val path = "/WFCD/warframe-items/master/data/json/Relics.json"

        val mapper = jacksonObjectMapper()
        val url = URL("$origin$path")
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val result = mutableListOf<RelicSet>()
        for (relicList in mapper.readValue(url, List::class.java)) {
            val converted = mapper.convertValue(relicList, RelicSet::class.java)
            if (converted.name.contains("Intact")) {
                val cleanName = converted.name.removeSuffix(" Intact")
                if (converted.rewards.isEmpty()) {
                    missingRewardList[cleanName]?.let {
                        converted.copy(name = cleanName, rewards =
                        it
                        )
                    }?.let { result.add(it) }
                } else {
                    result.add(converted.copy(name = cleanName))
                }
            }
        }
        return result
    }

    fun getRelicPerItemComp(searchText: String): List<RelicSet> {
        return relicData.filter {
            it.rewards.any { reward -> reward.item.name.startsWith(searchText, true) }
        }
    }

    fun getRelicTier(tier: RelicTier, remainingList: List<String>): List<RelicSet> {
        val dataRelic = relicData.filter { data -> data.name.startsWith(tier.name) }
        for (data in dataRelic) {
            for (reward in data.rewards) {
                reward.item.obtained =
                    !remainingList.any {
                        reward.item.name.startsWith(
                            it,
                            true
                        ) && reward.item.name != "Forma Blueprint"
                    }
            }
        }
        return dataRelic
    }

    companion object {
        suspend fun singleInstance(): PrimeRelicApi {
            val primeRelicApi = PrimeRelicApi()
            primeRelicApi.setRelicData()
            return primeRelicApi
        }
    }
}