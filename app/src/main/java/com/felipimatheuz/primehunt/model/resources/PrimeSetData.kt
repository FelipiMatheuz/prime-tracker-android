package com.felipimatheuz.primehunt.model.resources

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.felipimatheuz.primehunt.model.core.ItemPart
import com.felipimatheuz.primehunt.model.core.PrimeSet
import com.felipimatheuz.primehunt.util.apiData

class PrimeSetData(context: Context) {

    private val localData = context.getSharedPreferences("PRIME_SET_DATA", MODE_PRIVATE)

    fun updateListData(): List<PrimeSet> {
        checkDataUpdates()
        return getListSetData()
    }

    fun getListSetData(): List<PrimeSet> {
        val primeSetList = mutableListOf<PrimeSet>()
        val entries = localData.all.entries
        for (entry in entries) {
            val primeSet = jacksonObjectMapper().readValue(entry.value.toString(), PrimeSet::class.java)
            primeSetList.add(primeSet)
        }
        return primeSetList.sortedByDescending { it.released }
    }

    private fun checkDataUpdates() {
        val editor = localData.edit()
        for (data in apiData.getSetData()) {
            val itemSet = localData.getString(data.setName, null)
            if (itemSet.isNullOrEmpty()) {
                val json = jacksonObjectMapper().writeValueAsString(data)
                editor.putString(data.setName, json)
            } else {
                val primeSet = jacksonObjectMapper().readValue(itemSet, PrimeSet::class.java)
                primeSet.primeItems.forEachIndexed { index, primeItem ->
                    primeItem.name = data.primeItems[index].name
                }
                primeSet.imgLink = data.imgLink
                primeSet.status = data.status
                editor.putString(data.setName, jacksonObjectMapper().writeValueAsString(primeSet))
            }
        }
        editor.apply()
    }

    fun getPrimeSetData(name: String): PrimeSet {
        val setData = localData.getString(name, null)
        return jacksonObjectMapper().readValue(setData, PrimeSet::class.java)
    }

    private fun setPrimeSetData(name: String, primeSet: PrimeSet) {
        val editor = localData.edit()
        val json = jacksonObjectMapper().writeValueAsString(primeSet)
        editor.putString(name, json)
        editor.apply()
    }

    fun togglePrimeItem(primeSet: PrimeSet, primeItemName: String, itemPart: ItemPart?) {
        val primeSetItem = primeSet.primeItems.first { it.name == primeItemName }
        if (itemPart == null) {
            val obtained = primeSetItem.blueprint
            primeSetItem.blueprint = !obtained
        } else {
            val itemComp = primeSetItem.components.filter { it.part == itemPart }
            if (itemComp.size == 1) {
                val obtained = itemComp[0].obtained
                itemComp[0].obtained = !obtained
            } else {
                val getFalse = itemComp.firstOrNull { !it.obtained }
                if (getFalse == null) {
                    for (comp in itemComp) {
                        comp.obtained = false
                    }
                } else {
                    getFalse.obtained = true
                }
            }
        }
        setPrimeSetData(primeSet.setName, primeSet)
    }

    fun togglePrimeSet(primeSet: PrimeSet, obtained: Boolean) {
        primeSet.primeItems.forEach {
            it.blueprint = obtained
            for (comp in it.components) {
                comp.obtained = obtained
            }
        }
        setPrimeSetData(primeSet.setName, primeSet)
    }
}