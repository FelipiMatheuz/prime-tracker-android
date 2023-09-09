package com.felipimatheuz.primehunt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.model.core.PrimeItem
import com.felipimatheuz.primehunt.model.resources.OtherPrimeData
import com.felipimatheuz.primehunt.model.resources.PrimeSetData

class OverViewModel : ViewModel() {
    fun loadSet(context: Context, update: Boolean): List<PrimeItem> {
        val primeItems = mutableListOf<PrimeItem>()
        val primeData = if (update) {
            PrimeSetData(context).updateListData()
        } else {
            PrimeSetData(context).getListSetData()
        }

        primeData.forEach { primeSet ->
            primeItems.add(primeSet.warframe)
            primeItems.add(primeSet.primeItem1)
            if (primeSet.primeItem2 != null) {
                primeItems.add(primeSet.primeItem2!!)
            }
        }
        return primeItems
    }
    fun loadOther(context: Context, update: Boolean): List<PrimeItem> {
        return if (update) {
            OtherPrimeData(context).updateListData()
        } else {
            OtherPrimeData(context).getListOtherData()
        }
    }
}