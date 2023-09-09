package com.felipimatheuz.primehunt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.core.ItemPart
import com.felipimatheuz.primehunt.model.core.PrimeItem
import com.felipimatheuz.primehunt.model.resources.PrimeSetData
import com.felipimatheuz.primehunt.model.core.PrimeSet
import com.felipimatheuz.primehunt.model.core.PrimeStatus
import kotlinx.coroutines.flow.MutableStateFlow

class PrimeSetDetailViewModel(context: Context, primeName: String?) : ViewModel() {

    private val primeSetData = PrimeSetData(context)
    val primeSet = MutableStateFlow(primeSetData.getPrimeSetData(primeName!!))

    fun togglePrimeItem(primeSetName: String, itemName: PrimeItem, itemPart: ItemPart) {
        primeSetData.togglePrimeItem(primeSetName, itemName, itemPart)
    }
}