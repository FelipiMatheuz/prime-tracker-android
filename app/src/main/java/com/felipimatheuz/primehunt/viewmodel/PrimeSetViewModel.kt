package com.felipimatheuz.primehunt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.resources.PrimeSetData
import com.felipimatheuz.primehunt.model.core.PrimeSet
import com.felipimatheuz.primehunt.model.core.PrimeStatus
import kotlinx.coroutines.flow.MutableStateFlow

class PrimeSetViewModel(context: Context) : ViewModel() {

    private val primeSetData = PrimeSetData(context)
    val primeSets = MutableStateFlow(primeSetData.getListSetData())

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
}