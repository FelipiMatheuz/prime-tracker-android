package com.felipimatheuz.primehunt.data.mapper

import com.felipimatheuz.primehunt.data.local.entity.InventoryPartEntity

fun Iterable<InventoryPartEntity>.toInventoryMap(): Map<String, Int> {
    return associate { it.primePartId to it.quantity }
}
