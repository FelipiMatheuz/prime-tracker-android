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
    fun loadSet(update: Boolean): List<PrimeItem> {
        val primeItems = mutableListOf<PrimeItem>()
        val primeData = if (update) {
            primeSetData.updateListData()
        } else {
            primeSetData.getListSetData()
        }

        primeData.forEach { primeSet ->
            primeItems.addAll(primeSet.primeItems)
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