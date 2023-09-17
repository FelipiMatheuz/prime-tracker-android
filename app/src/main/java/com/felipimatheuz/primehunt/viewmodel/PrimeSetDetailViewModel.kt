package com.felipimatheuz.primehunt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.model.ItemPart
import com.felipimatheuz.primehunt.business.resources.PrimeSetData
import com.felipimatheuz.primehunt.business.util.updateCompStatus
import kotlinx.coroutines.flow.MutableStateFlow

class PrimeSetDetailViewModel(context: Context, primeName: String?) : ViewModel() {

    private val primeSetData = PrimeSetData(context)
    var primeSet = MutableStateFlow(primeSetData.getPrimeSetData(primeName!!))

    fun togglePrimeItem(primeItemName: String, itemPart: ItemPart?) {
        primeSetData.togglePrimeItem(primeSet.value, primeItemName, itemPart)
        updateIconStatus(primeItemName, itemPart)
    }

    fun updateIconStatus(primeItemName: String, itemPart: ItemPart?): Int {
        val primeItem = primeSet.value.primeItems.first { it.name == primeItemName }
        return updateCompStatus(if (itemPart != null)
            primeItem.components.filter { it.part == itemPart }
                .map { it.obtained }
        else
            listOf(primeItem.blueprint))
    }
}