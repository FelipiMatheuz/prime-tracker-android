package com.felipimatheuz.primehunt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.model.resources.OtherPrimeData
import com.felipimatheuz.primehunt.model.sets.PrimeItem
import com.felipimatheuz.primehunt.util.apiData

class OverViewModel : ViewModel() {
    fun loadSet(): List<PrimeItem> {
        val primeItems = mutableListOf<PrimeItem>()
        apiData.getSetData().forEach { primeSet ->
            primeItems.add(primeSet.warframe)
            primeItems.add(primeSet.primeItem1)
            if (primeSet.primeItem2 != null) {
                primeItems.add(primeSet.primeItem2!!)
            }
        }
        return primeItems
    }

    fun loadOther(context: Context): List<PrimeItem> {
        return OtherPrimeData(context).updateListData()
    }
}