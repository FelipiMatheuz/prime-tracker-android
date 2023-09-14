package com.felipimatheuz.primehunt.model.resources

import android.content.Context
import com.felipimatheuz.primehunt.model.core.ItemComponent
import com.felipimatheuz.primehunt.model.core.PrimeItem
import com.felipimatheuz.primehunt.util.apiOther

class OtherPrimeData(context: Context) {

    private val localData = context.getSharedPreferences("OTHER_PRIME_DATA", Context.MODE_PRIVATE)

    fun updateListData(): List<PrimeItem> {
        updateApiData()
        return getListOtherData()
    }

    private fun updateApiData() {
        apiOther.getOtherData().forEach { primeItem ->
            val bpObtained = localData.getBoolean(getFieldName(primeItem), false)
            primeItem.blueprint = bpObtained
            val distComp = primeItem.components.distinctBy { it.part }
            distComp.forEach { dist ->
                val itemComp = primeItem.components.filter { it.part == dist.part }
                itemComp.forEachIndexed { index, primeComp ->
                    val compObtained =
                        localData.getBoolean(getFieldName(primeItem, primeComp, index), false)
                    primeComp.obtained = compObtained
                }
            }
        }
    }

    private fun getFieldName(
        primeItem: PrimeItem,
        primeComp: ItemComponent? = null,
        index: Int? = null
    ) = "${primeItem.name}_${
        primeComp?.part ?: "BLUEPRINT"
    }${if (index != null) "_$index" else ""}"

    fun getListOtherData(): List<PrimeItem> = apiOther.getOtherData().sortedBy { it.name }

    private fun setStatusItem(name: String, value: Boolean) {
        val editor = localData.edit()
        editor.putBoolean(name, value)
        editor.apply()
    }

    fun togglePrimeItemComp(primeItem: PrimeItem, itemComponent: ItemComponent?) {
        if (itemComponent == null) {
            primeItem.blueprint = !primeItem.blueprint
            setStatusItem(getFieldName(primeItem), primeItem.blueprint)
        } else {
            val itemComp = primeItem.components.filter { it.part == itemComponent.part }
            if (itemComp.size == 1) {
                itemComp[0].obtained = !itemComp[0].obtained
                setStatusItem(getFieldName(primeItem, itemComp[0], 0), itemComp[0].obtained)
            } else {
                val getFalse = itemComp.firstOrNull { !it.obtained }
                if (getFalse == null) {
                    itemComp.forEachIndexed { index, comp ->
                        comp.obtained = false
                        setStatusItem(getFieldName(primeItem, comp, index), comp.obtained)
                    }
                } else {
                    val falseIndex = itemComp.indexOfFirst { !it.obtained }
                    getFalse.obtained = true
                    setStatusItem(getFieldName(primeItem, getFalse, falseIndex), getFalse.obtained)
                }
            }
        }
    }
}