package com.felipimatheuz.primehunt.viewmodel

import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.resources.PrimeSetData
import com.felipimatheuz.primehunt.business.util.PrimeFilter
import com.felipimatheuz.primehunt.model.PrimeSet
import com.felipimatheuz.primehunt.model.PrimeStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class PrimeSetViewModel @Inject constructor(private val primeSetData: PrimeSetData) : ViewModel() {

    private var primeSets = primeSetData.getListSetData()
    val primeSetsFiltered = MutableStateFlow(primeSets)

    fun refreshData() {
        primeSets = primeSetData.getListSetData()
    }

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