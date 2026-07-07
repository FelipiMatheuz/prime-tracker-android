package com.felipimatheuz.primehunt.business.resources

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.felipimatheuz.primehunt.business.util.getFieldName
import com.felipimatheuz.primehunt.business.util.primeSetList
import com.felipimatheuz.primehunt.model.ItemPart
import com.felipimatheuz.primehunt.model.PrimeSet
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrimeSetData @Inject constructor(@ApplicationContext context: Context) {

    private val localData = context.getSharedPreferences("PRIME_SET_DATA", MODE_PRIVATE)

    private val _dataInvalidator = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun getLocalData() = localData

    fun getListSetDataFlow(needsSync: Boolean = false): Flow<List<PrimeSet>> = _dataInvalidator
        .onStart { emit(Unit) }
        .map {
            if (needsSync) {
                updateApiData()
            }
            primeSetList.sortedByDescending { it.released }
        }

    private fun notifyDataChanged() {
        _dataInvalidator.tryEmit(Unit)
    }

    fun getPrimeSetData(primeSetName: String): PrimeSet =
        primeSetList.first { it.setName == primeSetName }

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
                            localData.getBoolean(
                                getFieldName(
                                    primeSet,
                                    primeItem,
                                    primeComp,
                                    index
                                ), false
                            )
                        primeComp.obtained = compObtained
                    }
                }
            }
        }
    }

    fun setStatusItem(name: String, value: Boolean) {
        localData.edit {
            putBoolean(name, value)
        }
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
                setStatusItem(
                    getFieldName(primeSet, primeSetItem, itemComp[0], 0),
                    itemComp[0].obtained
                )
            } else {
                val getFalse = itemComp.firstOrNull { !it.obtained }
                if (getFalse == null) {
                    itemComp.forEachIndexed { index, comp ->
                        comp.obtained = false
                        setStatusItem(
                            getFieldName(primeSet, primeSetItem, comp, index),
                            comp.obtained
                        )
                    }
                } else {
                    val falseIndex = itemComp.indexOfFirst { !it.obtained }
                    getFalse.obtained = true
                    setStatusItem(
                        getFieldName(primeSet, primeSetItem, getFalse, falseIndex),
                        getFalse.obtained
                    )
                }
            }
        }
        notifyDataChanged()
    }

    fun togglePrimeSet(primeSet: PrimeSet, obtained: Boolean) {
        localData.edit {
            primeSet.primeItems.forEach { primeItem ->
                primeItem.blueprint = obtained
                putBoolean(getFieldName(primeSet, primeItem), obtained)

                val distComp = primeItem.components.distinctBy { it.part }
                distComp.forEach { dist ->
                    val itemComp = primeItem.components.filter { it.part == dist.part }
                    itemComp.forEachIndexed { index, comp ->
                        comp.obtained = obtained
                        putBoolean(getFieldName(primeSet, primeItem, comp, index), obtained)
                    }
                }
            }
        }
        notifyDataChanged()
    }
}