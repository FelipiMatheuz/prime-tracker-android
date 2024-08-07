package com.felipimatheuz.primehunt.business.resources

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.felipimatheuz.primehunt.business.util.getFieldName
import com.felipimatheuz.primehunt.business.util.primeSetList
import com.felipimatheuz.primehunt.model.ItemPart
import com.felipimatheuz.primehunt.model.PrimeSet

class PrimeSetData(context: Context) {

    private val localData = context.getSharedPreferences("PRIME_SET_DATA", MODE_PRIVATE)

    fun updateListData(): List<PrimeSet> {
        updateApiData()
        return getListSetData()
    }

    fun getLocalData() = localData
    fun getListSetData(): List<PrimeSet> = primeSetList.sortedByDescending { it.released }

    fun getPrimeSetData(primeSetName: String): PrimeSet = primeSetList.first { it.setName == primeSetName }
    private fun updateApiData() {
        primeSetList.forEach { primeSet ->
            primeSet.primeItems.forEach { primeItem ->
                val bpObtained = localData.getBoolean(getFieldName(primeSet, primeItem), false)
                primeItem.blueprint = bpObtained
                val distComp = primeItem.components.distinctBy { it.part }
                distComp.forEach { dist ->
                    val itemComp = primeItem.components.filter { it.part == dist.part }
                    itemComp.forEachIndexed { index, primeComp ->
                        val compObtained =
                            localData.getBoolean(getFieldName(primeSet, primeItem, primeComp, index), false)
                        primeComp.obtained = compObtained
                    }
                }
            }
        }
    }

    fun setStatusItem(name: String, value: Boolean) {
        val editor = localData.edit()
        editor.putBoolean(name, value)
        editor.apply()
    }

    fun togglePrimeItem(primeSet: PrimeSet, primeItemName: String, itemPart: ItemPart?) {
        val primeSetItem = primeSet.primeItems.first { it.name == primeItemName }
        if (itemPart == null) {
            val obtained = primeSetItem.blueprint
            primeSetItem.blueprint = !obtained
            setStatusItem(getFieldName(primeSet, primeSetItem), primeSetItem.blueprint)
        } else {
            val itemComp = primeSetItem.components.filter { it.part == itemPart }
            if (itemComp.size == 1) {
                val obtained = itemComp[0].obtained
                itemComp[0].obtained = !obtained
                setStatusItem(getFieldName(primeSet, primeSetItem, itemComp[0], 0), itemComp[0].obtained)
            } else {
                val getFalse = itemComp.firstOrNull { !it.obtained }
                if (getFalse == null) {
                    itemComp.forEachIndexed { index, comp ->
                        comp.obtained = false
                        setStatusItem(getFieldName(primeSet, primeSetItem, comp, index), comp.obtained)
                    }
                } else {
                    val falseIndex = itemComp.indexOfFirst { !it.obtained }
                    getFalse.obtained = true
                    setStatusItem(getFieldName(primeSet, primeSetItem, getFalse, falseIndex), getFalse.obtained)
                }
            }
        }
    }

    fun togglePrimeSet(primeSet: PrimeSet, obtained: Boolean) {
        val editor = localData.edit()
        primeSet.primeItems.forEach { primeItem ->
            primeItem.blueprint = obtained
            editor.putBoolean(getFieldName(primeSet, primeItem), obtained)

            val distComp = primeItem.components.distinctBy { it.part }
            distComp.forEach { dist ->
                val itemComp = primeItem.components.filter { it.part == dist.part }
                itemComp.forEachIndexed { index, comp ->
                    comp.obtained = obtained
                    editor.putBoolean(getFieldName(primeSet, primeItem, comp, index), obtained)
                }
            }
        }
        editor.apply()
    }
}