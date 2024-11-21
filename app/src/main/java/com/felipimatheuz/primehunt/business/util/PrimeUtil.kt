package com.felipimatheuz.primehunt.business.util

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.*
import com.felipimatheuz.primehunt.ui.theme.*
import kotlin.math.roundToInt

fun updateStatusColor(primeSet: PrimeSet): Color {
    val totalPercent = primeSet.primeItems.sumOf { getPercent(it) } / primeSet.primeItems.size
    return if (totalPercent == 0) {
        Zero
    } else if (totalPercent <= 50) {
        Low
    } else if (totalPercent < 100) {
        High
    } else {
        Complete
    }
}

fun updateStatusColor(
    item: PrimeItem
): Color {
    val percent = getPercent(item)
    return if (percent == 0) {
        Zero
    } else if (percent <= 50) {
        Low
    } else if (percent < 100) {
        High
    } else {
        Complete
    }
}

fun getPercent(target: PrimeItem): Int {
    val total = target.components.size + 1
    var obtained = target.components.count {
        it.obtained
    }
    if (target.blueprint) {
        obtained++
    }
    val percent = (obtained.toFloat() / total.toFloat()) * 100
    return percent.roundToInt()
}

fun getCompCount(primeItem: PrimeItem): Map<ItemPart?, Int> {
    return primeItem.components.groupingBy { it.part }.eachCount().filter { it.value > 1 }
}

fun formatItemPartText(context: Context, part: ItemPart, quantity: Int?): String {
    val quantityText = if (quantity != null && quantity > 1) {
        " ${quantity}x"
    } else {
        ""
    }
    return context.getString(part.text).plus(quantityText)
}

fun getRelicList(searchText: String): List<RelicSet> {
    return relicList.filter {
        it.rewards.any { reward -> reward.item.name.startsWith(searchText, true) }
    }.sortedBy { it.vaulted }
}

fun getCompName(context: Context, primeItem: PrimeItem, comp: ItemComponent?): String {
    val compName = if (comp == null) {
        if (primeItem.name == "Kavasa") {
            "Kubrow Collar BLUEPRINT"
        } else {
            "BLUEPRINT"
        }
    } else if (comp.part == ItemPart.CIRCUIT) {
        ItemPart.SYSTEMS.toString()
    } else if (comp.part == ItemPart.ULIMB) {
        "UPPER LIMB"
    } else if (comp.part == ItemPart.LLIMB) {
        "LOWER LIMB"
    } else {
        comp.part.toString()
    }
    return context.getString(R.string.prime_template, primeItem.name).plus(" ").plus(compName)
}

@Composable
fun getColorForeground(rewards: List<Reward>, searchText: String): Color? {
    val result = rewards.filter { it.item.name.contains(searchText, true) }
    return if (result.isEmpty()) {
        null
    } else {
        return if (isSystemInDarkTheme()) {
            when (result[0].chance) {
                25.33f -> CommonDark
                11f -> UncommonDark
                2f -> RareDark
                else -> CommonDark
            }
        } else {
            when (result[0].chance) {
                25.33f -> Common
                11f -> Uncommon
                2f -> Rare
                else -> Common
            }
        }
    }
}

fun updateCompStatus(obtainedList: List<Boolean>): Int {
    val numberObtained = obtainedList.count { it }
    return if (numberObtained == 0) {
        0
    } else if (numberObtained < obtainedList.size) {
        R.drawable.progress_circle
    } else {
        R.drawable.check_circle
    }
}

fun translateComponent(context: Context, s: String): String {
    val compTranslate = when (s) {
        "Blueprint" -> context.getString(R.string.comp_blueprint)
        "Blade" -> context.getString(R.string.comp_blade)
        "Neuroptics" -> context.getString(R.string.comp_neuroptics)
        "Chassis" -> context.getString(R.string.comp_chassis)
        "Systems" -> context.getString(R.string.comp_systems)
        "Barrel" -> context.getString(R.string.comp_barrel)
        "Disc" -> context.getString(R.string.comp_disc)
        "Handle" -> context.getString(R.string.comp_handle)
        "Ornament" -> context.getString(R.string.comp_ornament)
        "Receiver" -> context.getString(R.string.comp_receiver)
        "Stock" -> context.getString(R.string.comp_stock)
        "Link" -> context.getString(R.string.comp_link)
        "Cerebrum" -> context.getString(R.string.comp_cerebrum)
        "Carapace" -> context.getString(R.string.comp_carapace)
        "Gauntlet" -> context.getString(R.string.comp_gauntlet)
        "Pouch" -> context.getString(R.string.comp_pouch)
        "Stars" -> context.getString(R.string.comp_stars)
        "Hilt" -> context.getString(R.string.comp_hilt)
        "Harness" -> context.getString(R.string.comp_harness)
        "Wings" -> context.getString(R.string.comp_wings)
        "Band" -> context.getString(R.string.comp_band)
        "Buckle" -> context.getString(R.string.comp_buckle)
        "Head" -> context.getString(R.string.comp_head)
        "Grip" -> context.getString(R.string.comp_grip)
        "String" -> context.getString(R.string.comp_string)
        "Lower Limb" -> context.getString(R.string.comp_llinb)
        "Upper Limb" -> context.getString(R.string.comp_ulimb)
        "Guard" -> context.getString(R.string.comp_guard)
        "Boot" -> context.getString(R.string.comp_boot)
        "Chain" -> context.getString(R.string.comp_chain)
        else -> return ""
    }
    return ": $compTranslate"
}

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