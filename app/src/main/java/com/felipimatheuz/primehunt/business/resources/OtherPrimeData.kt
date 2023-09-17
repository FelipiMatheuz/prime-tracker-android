package com.felipimatheuz.primehunt.business.resources

import android.content.Context
import com.felipimatheuz.primehunt.model.ItemComponent
import com.felipimatheuz.primehunt.model.PrimeItem
import com.felipimatheuz.primehunt.business.util.apiOther
import com.felipimatheuz.primehunt.business.util.getFieldName

class OtherPrimeData(context: Context) {

    private val localData = context.getSharedPreferences("OTHER_PRIME_DATA", Context.MODE_PRIVATE)

    fun updateListData(): List<PrimeItem> {
        updateApiData()
        return getListOtherData()
    }

    private fun updateApiData() {
        apiOther.getOtherData().forEach { primeItem ->
            val bpObtained = localData.getBoolean(getFieldName(primeItem = primeItem), false)
            primeItem.blueprint = bpObtained
            val distComp = primeItem.components.distinctBy { it.part }
            distComp.forEach { dist ->
                val itemComp = primeItem.components.filter { it.part == dist.part }
                itemComp.forEachIndexed { index, primeComp ->
                    val compObtained =
                        localData.getBoolean(
                            getFieldName(primeItem = primeItem, primeComp = primeComp, index = index),
                            false
                        )
                    primeComp.obtained = compObtained
                }
            }
        }
    }

    fun getLocalData() = localData
    fun getListOtherData(): List<PrimeItem> = apiOther.getOtherData().sortedBy { it.name }

    fun setStatusItem(name: String, value: Boolean) {
        val editor = localData.edit()
        editor.putBoolean(name, value)
        editor.apply()
    }

    fun togglePrimeItemComp(primeItem: PrimeItem, itemComponent: ItemComponent?) {
        if (itemComponent == null) {
            primeItem.blueprint = !primeItem.blueprint
            setStatusItem(getFieldName(primeItem = primeItem), primeItem.blueprint)
        } else {
            val itemComp = primeItem.components.filter { it.part == itemComponent.part }
            if (itemComp.size == 1) {
                itemComp[0].obtained = !itemComp[0].obtained
                setStatusItem(
                    getFieldName(primeItem = primeItem, primeComp = itemComp[0], index = 0),
                    itemComp[0].obtained
                )
            } else {
                val getFalse = itemComp.firstOrNull { !it.obtained }
                if (getFalse == null) {
                    itemComp.forEachIndexed { index, comp ->
                        comp.obtained = false
                        setStatusItem(
                            getFieldName(primeItem = primeItem, primeComp = comp, index = index),
                            comp.obtained
                        )
                    }
                } else {
                    val falseIndex = itemComp.indexOfFirst { !it.obtained }
                    getFalse.obtained = true
                    setStatusItem(
                        getFieldName(primeItem = primeItem, primeComp = getFalse, index = falseIndex),
                        getFalse.obtained
                    )
                }
            }
        }
    }
}