package com.felipimatheuz.primehunt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.model.core.ItemComponent
import com.felipimatheuz.primehunt.model.core.ItemPart
import com.felipimatheuz.primehunt.model.core.PrimeItem
import com.felipimatheuz.primehunt.model.resources.OtherPrimeData
import com.felipimatheuz.primehunt.util.PrimeFilter
import com.felipimatheuz.primehunt.util.updateCompStatus
import kotlinx.coroutines.flow.MutableStateFlow

class OtherPrimeViewModel(context: Context) : ViewModel() {

    private val otherPrimeData = OtherPrimeData(context)
    private val primeItems = otherPrimeData.getListOtherData()
    val otherPrimesFiltered = MutableStateFlow(primeItems)

    fun filterOtherPrime(searchText: String, primeFilter: PrimeFilter) {
        var primeList = when (primeFilter) {
            PrimeFilter.COMPLETE -> primeItems.filter { isComplete(it) }
            PrimeFilter.INCOMPLETE -> primeItems.filter { !isComplete(it) }
            else -> primeItems
        }
        if (searchText.isNotEmpty()) {
            primeList = primeList.filter { primeItem ->
                primeItem.name.contains(searchText, true)
            }
        }
        otherPrimesFiltered.value = primeList
    }

    private fun isComplete(primeItem: PrimeItem) = primeItem.blueprint &&
            primeItem.components.all { itemComponent -> itemComponent.obtained }

    fun hasAnotherPrimeItem(primeItem: PrimeItem) =
        primeItem.components.any { it.part == ItemPart.VASTO || it.part == ItemPart.LEX || it.part == ItemPart.BRONCO }

    fun isAllChecked(primeItem: PrimeItem) =
        primeItem.blueprint && primeItem.components.all { it.obtained }

    fun togglePrimeItem(primeItem: PrimeItem) {
        otherPrimeData.togglePrimeItem(primeItem, !isAllChecked(primeItem))
    }

    fun togglePrimeItemComp(primeItem: PrimeItem, primeItemComp: ItemComponent?) {
        otherPrimeData.togglePrimeItemComp(primeItem, primeItemComp)
    }

    fun updateIconStatus(primeItem: PrimeItem, itemPart: ItemPart?): Int {
        return updateCompStatus(if (itemPart != null)
            primeItem.components.filter { it.part == itemPart }
                .map { it.obtained }
        else
            listOf(primeItem.blueprint))
    }
}