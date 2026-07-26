package com.felipimatheuz.primehunt.ui.viewmodel.primedetail

import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.ui.mvi.MviIntent
import com.felipimatheuz.primehunt.ui.mvi.MviState

data class PrimeDetailState(
    val primeSet: PrimeSetDomain? = null,
    val isLoading: Boolean = true
) : MviState

sealed class PrimeDetailIntent : MviIntent {
    data class UpdateQuantity(val partId: String, val delta: Int) : PrimeDetailIntent()
    data class UpdateSetQuantity(val delta: Int) : PrimeDetailIntent()
}
