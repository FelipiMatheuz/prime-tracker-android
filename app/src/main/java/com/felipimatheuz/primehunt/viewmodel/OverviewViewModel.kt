package com.felipimatheuz.primehunt.viewmodel

import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.business.resources.OtherPrimeData
import com.felipimatheuz.primehunt.business.resources.PrimeSetData
import com.felipimatheuz.primehunt.model.PrimeItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val primeSetData: PrimeSetData,
    private val otherPrimeData: OtherPrimeData
) : ViewModel() {
    suspend fun loadSet(update: Boolean): List<PrimeItem> {
        val primeItems = mutableListOf<PrimeItem>()
        val primeData = primeSetData.getListSetDataFlow(update)

        primeData.collect {
            it.forEach { primeSet ->
                primeItems.addAll(primeSet.primeItems)
            }
        }
        return primeItems
    }

    fun loadOther(update: Boolean): List<PrimeItem> {
        return if (update) {
            otherPrimeData.updateListData()
        } else {
            otherPrimeData.getListOtherData()
        }
    }
}