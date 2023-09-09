package com.felipimatheuz.primehunt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.core.PrimeSet
import com.felipimatheuz.primehunt.model.core.PrimeStatus
import com.felipimatheuz.primehunt.model.resources.PrimeSetData
import com.felipimatheuz.primehunt.util.PrimeFilter

class PrimeSetViewModel(context: Context) : ViewModel() {

    private val primeSetData = PrimeSetData(context)
    private val primeSets = primeSetData.getListSetData()

    fun getStatusTextRes(status: PrimeStatus): Int {
        val statusText = when (status) {
            PrimeStatus.VAULT -> R.string.status_vault
            PrimeStatus.ACTIVE -> R.string.status_active
            PrimeStatus.BARO -> R.string.status_baro
            PrimeStatus.RESURGENCE -> R.string.status_resurgence
        }
        return statusText
    }

    fun togglePrimeSet(primeSet: PrimeSet, checkAll: Boolean) {
        primeSetData.togglePrimeSet(primeSet, checkAll)
    }

    fun filterPrimeSet(searchText: String, primeFilter: PrimeFilter): List<PrimeSet> {
        var primeList = when (primeFilter) {
            PrimeFilter.SHOW_ALL -> primeSets
            PrimeFilter.COMPLETE -> primeSets.filter { isComplete(it) }
            PrimeFilter.INCOMPLETE -> primeSets.filter { !isComplete(it) }
            PrimeFilter.AVAILABLE -> primeSets.filter { it.status != PrimeStatus.VAULT }
            PrimeFilter.UNAVAILABLE -> primeSets.filter { it.status == PrimeStatus.VAULT }
        }
        if (searchText.isNotEmpty()) {
            primeList = primeList.filter {
                it.warframe.name.contains(searchText, true)
                        || it.primeItem1.name.contains(searchText, true)
                        || it.primeItem2?.name?.contains(searchText, true) == true
            }
        }
        return primeList
    }

    private fun isComplete(it: PrimeSet) = (it.warframe.blueprint &&
            it.warframe.components.any { itemComponent -> itemComponent.obtained }
            && it.primeItem1.blueprint &&
            it.primeItem1.components.any { itemComponent -> itemComponent.obtained }
            && (if (it.primeItem2 != null) {
        it.primeItem2!!.blueprint && it.primeItem2!!.components.any { itemComponent -> itemComponent.obtained }
    } else {
        true
    }))
}