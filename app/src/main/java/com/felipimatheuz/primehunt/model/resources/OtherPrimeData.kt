package com.felipimatheuz.primehunt.model.resources

import android.content.Context
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.felipimatheuz.primehunt.model.core.ItemComponent
import com.felipimatheuz.primehunt.model.core.PrimeItem
import com.felipimatheuz.primehunt.util.otherData

class OtherPrimeData(context: Context) {

    private val localData = context.getSharedPreferences("OTHER_PRIME_DATA", Context.MODE_PRIVATE)

    fun updateListData(): List<PrimeItem> {
        checkDataUpdates()
        return getListOtherData()
    }

    private fun checkDataUpdates() {
        val editor = localData.edit()
        for (data in otherData) {
            val otherItem = localData.getString(data.name, null)
            if (otherItem.isNullOrEmpty()) {
                val json = jacksonObjectMapper().writeValueAsString(data)
                editor.putString(data.name, json)
            } else {
                val primeItem = jacksonObjectMapper().readValue(otherItem, PrimeItem::class.java)
                primeItem.name = data.name
                editor.putString(data.name, jacksonObjectMapper().writeValueAsString(primeItem))
            }
        }
        editor.apply()
    }

    fun getListOtherData(): List<PrimeItem> {
        val primeOtherList = mutableListOf<PrimeItem>()
        val entries = localData.all.entries
        for (entry in entries) {
            val primeItem = jacksonObjectMapper().readValue(entry.value.toString(), PrimeItem::class.java)
            primeOtherList.add(primeItem)
        }
        return primeOtherList.sortedBy { it.name }
    }

    private fun setListOtherData(name: String, primeItem: PrimeItem) {
        val editor = localData.edit()
        val json = jacksonObjectMapper().writeValueAsString(primeItem)
        editor.putString(name, json)
        editor.apply()
    }

    fun togglePrimeItem(primeItem: PrimeItem) {
        val obtained = !primeItem.blueprint
        changeStatusItem(primeItem, obtained)
        setListOtherData(primeItem.name, primeItem)
    }

    fun togglePrimeItemComp(primeItem: PrimeItem, itemComponent: ItemComponent?) {
        changeStatusComponent(primeItem, itemComponent)
        setListOtherData(primeItem.name, primeItem)
    }

    private fun changeStatusItem(primeItem: PrimeItem, obtained: Boolean) {
        primeItem.blueprint = obtained
        for (comp in primeItem.components) {
            comp.obtained = obtained
        }
    }

    private fun changeStatusComponent(primeItem: PrimeItem, itemComponent: ItemComponent?) {
        if (itemComponent == null) {
            primeItem.blueprint = !primeItem.blueprint
        } else {
            val filterComp = primeItem.components.filter { it.part == itemComponent.part }
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
}