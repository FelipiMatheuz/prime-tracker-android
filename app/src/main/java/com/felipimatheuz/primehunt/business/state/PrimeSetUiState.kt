package com.felipimatheuz.primehunt.business.state

import com.felipimatheuz.primehunt.model.PrimeSet

data class PrimeSetUiState(
    val primeSets: List<PrimeSet> = emptyList(),
    val selectedPrimeSet: String = "",
    val queryFilter: String = ""
)