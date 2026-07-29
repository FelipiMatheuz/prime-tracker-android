package com.felipimatheuz.primehunt.business.resources

import android.content.Context
import androidx.core.content.edit
import com.felipimatheuz.primehunt.business.util.getFieldName
import com.felipimatheuz.primehunt.business.util.otherPrimeList
import com.felipimatheuz.primehunt.model.PrimeItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtherPrimeData @Inject constructor(@ApplicationContext context: Context) {

    private val localData = context.getSharedPreferences("OTHER_PRIME_DATA", Context.MODE_PRIVATE)

    fun updateListData(): List<PrimeItem> {
        updateApiData()
        return getListOtherData()
    }

    private fun updateApiData() {
        otherPrimeList.forEach { primeItem ->
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
    fun getListOtherData(): List<PrimeItem> = otherPrimeList.sortedBy { it.name }

    fun setStatusItem(name: String, value: Boolean) {
        localData.edit {
            putBoolean(name, value)
        }
    }
}