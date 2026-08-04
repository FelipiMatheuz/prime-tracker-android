package com.felipimatheuz.primehunt.business.util

import com.felipimatheuz.primehunt.model.ItemComponent
import com.felipimatheuz.primehunt.model.PrimeItem
import com.felipimatheuz.primehunt.model.PrimeSet

fun getFieldName(
    primeSet: PrimeSet? = null,
    primeItem: PrimeItem,
    primeComp: ItemComponent? = null,
    index: Int? = null
) = "${if (primeSet?.setName != null) "${primeSet.setName}_" else ""}${primeItem.name}_${
    primeComp?.part ?: "BLUEPRINT"
}${if (index != null) "_$index" else ""}"