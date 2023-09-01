package com.felipimatheuz.primehunt.model.resources

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.felipimatheuz.primehunt.model.sets.ItemPart
import com.felipimatheuz.primehunt.model.sets.PrimeItem
import com.felipimatheuz.primehunt.model.sets.PrimeSet
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
            val itemSet = localData.getString(data.warframe.name, null)
            if (itemSet.isNullOrEmpty()) {
                val json = jacksonObjectMapper().writeValueAsString(data)
                editor.putString(data.warframe.name, json)
            } else {
                val primeSet = jacksonObjectMapper().readValue(itemSet, PrimeSet::class.java)
                primeSet.warframe.name = data.warframe.name
                primeSet.primeItem1.name = data.primeItem1.name
                if (data.primeItem2 != null) {
                    primeSet.primeItem2?.name = data.primeItem2!!.name
                }
                primeSet.imgLink = data.imgLink
                primeSet.status = data.status
                editor.putString(data.warframe.name, jacksonObjectMapper().writeValueAsString(primeSet))
            }
        }
        editor.apply()
    }

    private fun getPrimeSetData(name: String): PrimeSet {
        val setData = localData.getString(name, null)
        return jacksonObjectMapper().readValue(setData, PrimeSet::class.java)
    }

    private fun setPrimeSetData(name: String, primeSet: PrimeSet) {
        val editor = localData.edit()
        val json = jacksonObjectMapper().writeValueAsString(primeSet)
        editor.putString(name, json)
        editor.apply()
    }

    fun togglePrimeItem(primeSetName: String, itemName: PrimeItem, itemPart: ItemPart?) {
        val primeSet = getPrimeSetData(primeSetName)
        changeStatusItem(itemName, itemPart)

        when (itemName.name) {
            primeSet.warframe.name -> primeSet.warframe = itemName
            primeSet.primeItem1.name -> primeSet.primeItem1 = itemName
            primeSet.primeItem2?.name -> primeSet.primeItem2 = itemName
        }
        setPrimeSetData(primeSetName, primeSet)
    }

    private fun changeStatusItem(primeItem: PrimeItem, itemPart: ItemPart?) {
        if (itemPart == null) {
            val obtained = primeItem.blueprint
            primeItem.blueprint = !obtained
        } else {
            val filterComp = primeItem.components.filter { it.part == itemPart }
            if (filterComp.size == 1) {
                val obtained = filterComp[0].obtained
                filterComp[0].obtained = !obtained
            } else {
                val getFalse = filterComp.firstOrNull { !it.obtained }
                if (getFalse == null) {
                    for (comp in filterComp) {
                        comp.obtained = false
                    }
                } else {
                    getFalse.obtained = true
                }
            }
        }
    }

    fun togglePrimeSet(primeSet: PrimeSet) {
        val obtained = !primeSet.warframe.blueprint
        changeStatusSet(primeSet, obtained)
        setPrimeSetData(primeSet.warframe.name, primeSet)
    }

    private fun changeStatusSet(primeSet: PrimeSet, obtained: Boolean) {
        changeStatusComponents(primeSet.warframe, obtained)
        changeStatusComponents(primeSet.primeItem1, obtained)
        if (primeSet.primeItem2 != null) {
            changeStatusComponents(primeSet.primeItem2!!, obtained)
        }
    }

    private fun changeStatusComponents(primeItem: PrimeItem, obtained: Boolean) {
        primeItem.blueprint = obtained
        for (comp in primeItem.components) {
            comp.obtained = obtained
        }
    }
}