package com.felipimatheuz.primehunt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.core.PrimeSet
import com.felipimatheuz.primehunt.model.core.PrimeStatus
import com.felipimatheuz.primehunt.model.resources.PrimeSetData
import com.felipimatheuz.primehunt.util.PrimeFilter
import kotlinx.coroutines.flow.MutableStateFlow

class PrimeSetViewModel(context: Context) : ViewModel() {

    private val primeSetData = PrimeSetData(context)
    private val primeSets = primeSetData.getListSetData()
    val primeSetsFiltered = MutableStateFlow(primeSets)

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

    fun filterPrimeSet(searchText: String, primeFilter: PrimeFilter) {
        var primeList = when (primeFilter) {
            PrimeFilter.SHOW_ALL -> primeSets
            PrimeFilter.COMPLETE -> primeSets.filter { isComplete(it) }
            PrimeFilter.INCOMPLETE -> primeSets.filter { !isComplete(it) }
            PrimeFilter.AVAILABLE -> primeSets.filter { it.status != PrimeStatus.VAULT }
            PrimeFilter.UNAVAILABLE -> primeSets.filter { it.status == PrimeStatus.VAULT }
        }
        if (searchText.isNotEmpty()) {
            primeList = primeList.filter { primeSet ->
                primeSet.primeItems.any { it.name.contains(searchText, true) }
            }
        }
        primeSetsFiltered.value = primeList
    }

    private fun isComplete(primeSet: PrimeSet) = primeSet.primeItems.all { it.blueprint } &&
            primeSet.primeItems.all { primeItem ->
                primeItem.components.all { itemComponent -> itemComponent.obtained }
            }
}