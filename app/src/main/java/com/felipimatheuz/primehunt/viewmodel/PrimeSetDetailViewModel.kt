package com.felipimatheuz.primehunt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipimatheuz.primehunt.business.resources.PrimeSetData
import com.felipimatheuz.primehunt.business.util.updateCompStatus
import com.felipimatheuz.primehunt.model.ItemPart
import com.felipimatheuz.primehunt.model.PrimeSet
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = PrimeSetDetailViewModel.Factory::class)
class PrimeSetDetailViewModel @AssistedInject constructor(
    private val primeSetData: PrimeSetData,
    @Assisted private val primeName: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(primeName: String): PrimeSetDetailViewModel
    }

    val primeSet: StateFlow<PrimeSet?> = primeSetData.getListSetDataFlow()
        .map { list -> list.firstOrNull { it.setName == primeName } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = primeSetData.getPrimeSetData(primeName)
        )

    fun togglePrimeItem(primeItemName: String, itemPart: ItemPart?) {
        primeSet.value?.let {
            primeSetData.togglePrimeItem(it, primeItemName, itemPart)
        }
    }

    fun updateIconStatus(primeItemName: String, itemPart: ItemPart?): Int {
        val currentSet = primeSet.value ?: return 0
        val primeItem = currentSet.primeItems.first { it.name == primeItemName }
        return updateCompStatus(
            if (itemPart != null)
                primeItem.components.filter { it.part == itemPart }
                    .map { it.obtained }
            else
                listOf(primeItem.blueprint)
        )
    }
}