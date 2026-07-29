package com.felipimatheuz.primehunt.business.util

import android.content.Context
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.model.ItemComponent
import com.felipimatheuz.primehunt.model.PrimeItem
import com.felipimatheuz.primehunt.model.PrimeSet

fun isItemCompleted(primeItem: PrimeItem): Int {
    return if (primeItem.blueprint && primeItem.components.all { pi -> pi.obtained }) {
        2
    } else if (primeItem.blueprint || primeItem.components.any { pi -> pi.obtained }) {
        1
    } else {
        0
    }
}

fun getFieldName(
    primeSet: PrimeSet? = null,
    primeItem: PrimeItem,
    primeComp: ItemComponent? = null,
    index: Int? = null
) = "${if (primeSet?.setName != null) "${primeSet.setName}_" else ""}${primeItem.name}_${
    primeComp?.part ?: "BLUEPRINT"
}${if (index != null) "_$index" else ""}"

fun dpFromPx(context: Context, px: Int): Dp {
    return (px / context.resources.displayMetrics.density).dp
}