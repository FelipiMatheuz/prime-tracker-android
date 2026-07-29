package com.felipimatheuz.primehunt.business.resources

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import com.felipimatheuz.primehunt.business.util.getFieldName
import com.felipimatheuz.primehunt.business.util.primeSetList
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

    fun getLocalData(): SharedPreferences? = localData

    fun getListSetDataFlow(needsSync: Boolean = false): Flow<List<PrimeSet>> = _dataInvalidator
        .onStart { emit(Unit) }
        .map {
            if (needsSync) {
                updateApiData()
            }
            primeSetList.sortedByDescending { it.released }
        }

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
}