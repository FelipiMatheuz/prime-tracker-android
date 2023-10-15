package com.felipimatheuz.primehunt.business.external

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.felipimatheuz.primehunt.model.PrimeItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.net.URL


class OtherPrimeApi {
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    private var otherData: List<PrimeItem> = listOf()

    suspend fun setOtherData() {
        return coroutineScope {
            val resultData = async(defaultDispatcher) { loadData() }
            otherData = resultData.await()
        }
    }

    fun getOtherData(): List<PrimeItem> = otherData

    private fun loadData(): List<PrimeItem> {
        val mapper = jacksonObjectMapper()
            val origin = "https://raw.githubusercontent.com"
            val path = "/FelipiMatheuz/WPH/main/api/other_primes.json"
            val url = URL("$origin$path")
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            val result = mutableListOf<PrimeItem>()
            for (primeItemJson in mapper.readValue(url, List::class.java)) {
                result.add(mapper.convertValue(primeItemJson, PrimeItem::class.java))
            }
            return result
    }

    companion object {
        suspend fun singleInstance(): OtherPrimeApi {
            val otherPrimeApi = OtherPrimeApi()
            otherPrimeApi.setOtherData()
            return otherPrimeApi
        }
    }
}