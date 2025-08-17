package com.felipimatheuz.primehunt.viewmodel

import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.model.ItemPart
import com.felipimatheuz.primehunt.business.resources.PrimeSetData
import com.felipimatheuz.primehunt.business.util.updateCompStatus
import com.felipimatheuz.primehunt.model.PrimeSet
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel(assistedFactory = PrimeSetDetailViewModel.Factory::class)
class PrimeSetDetailViewModel @AssistedInject constructor(
    private val primeSetData: PrimeSetData,
    @Assisted private val primeName: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(primeName: String): PrimeSetDetailViewModel
    }

    private val _primeSet = MutableStateFlow(primeSetData.getPrimeSetData(primeName))
    val primeSet: StateFlow<PrimeSet> = _primeSet

    fun togglePrimeItem(primeItemName: String, itemPart: ItemPart?) {
        primeSetData.togglePrimeItem(_primeSet.value, primeItemName, itemPart)
        _primeSet.value = primeSetData.getPrimeSetData(primeName)
    }

    fun updateIconStatus(primeItemName: String, itemPart: ItemPart?): Int {
        val primeItem = _primeSet.value.primeItems.first { it.name == primeItemName }
        return updateCompStatus(
            if (itemPart != null)
                primeItem.components.filter { it.part == itemPart }
                    .map { it.obtained }
            else
                listOf(primeItem.blueprint)
        )
    }
}