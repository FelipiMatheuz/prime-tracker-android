package com.felipimatheuz.primehunt.model.external

import androidx.annotation.Keep
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.felipimatheuz.primehunt.model.core.*
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
            relicData = resultData.await().filter { it.name.contains("Intact") }
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
        for (l in mapper.readValue(url, List::class.java)) {
            val converted = mapper.convertValue(l, RelicSet::class.java)
            if(converted.name.contains("Intact")){
                result.add(converted)
            }
        }
        return result
    }

    fun getRelicPerItemComp(searchText: String): List<RelicSet> {
        return relicData.filter {
            it.rewards.any { reward -> reward.item.name.startsWith(searchText, true) }
        }
    }

    fun getRelics(tier: RelicTier, remainingList: List<String>): List<RelicItem> {
        val dataRelic = relicData.filter { data -> data.name.startsWith(tier.name) }
        val relicItemList: MutableList<RelicItem> = mutableListOf()
        for (data in dataRelic) {
            relicItemList.add(
                RelicItem(
                    name = data.name.subSequence(data.name.indexOf(" ") + 1, data.name.lastIndexOf(" ")).toString(),
                    quantity = getQuantity(data.rewards, remainingList),
                    hasForma = checkFormaAsReward(data.rewards),
                    vaulted = data.vaulted
                )
            )
        }
        return relicItemList.sortedByDescending { it.quantity }
    }

    private fun getQuantity(rewards: List<Reward>, remainingList: List<String>): Int {
        var remainingCount = 0

        for (reward in rewards) {
            for (search in remainingList) {
                if (reward.item.name.startsWith(search, true)) {
                    remainingCount++
                    break
                }
            }
        }
        return remainingCount
    }

    private fun checkFormaAsReward(rewards: List<Reward>): Boolean {
        return rewards.any { it.item.name == "Forma Blueprint" }
    }

    companion object {
        suspend fun singleInstance(): PrimeRelicApi {
            val primeRelicApi = PrimeRelicApi()
            primeRelicApi.setRelicData()
            return primeRelicApi
        }
    }
}