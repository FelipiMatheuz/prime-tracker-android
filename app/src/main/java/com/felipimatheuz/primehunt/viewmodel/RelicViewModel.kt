package com.felipimatheuz.primehunt.viewmodel

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.core.*
import com.felipimatheuz.primehunt.model.resources.OtherPrimeData
import com.felipimatheuz.primehunt.model.resources.PrimeSetData
import com.felipimatheuz.primehunt.ui.theme.Common
import com.felipimatheuz.primehunt.ui.theme.Rare
import com.felipimatheuz.primehunt.ui.theme.Uncommon
import com.felipimatheuz.primehunt.util.PrimeFilter
import com.felipimatheuz.primehunt.util.apiRelic
import com.felipimatheuz.primehunt.util.translateComponent

class RelicViewModel(context: Context) : ViewModel() {
    private val remainingList = searchList(context)

    fun getListTier(tier: RelicTier, primeFilter: PrimeFilter, searchText: String): List<Relic> {
        var tierData = apiRelic.getRelics(tier, remainingList)
        tierData = when (primeFilter) {
            PrimeFilter.AVAILABLE -> tierData.filter { !it.vaulted }
            PrimeFilter.UNAVAILABLE -> tierData.filter { it.vaulted }
            PrimeFilter.COMPLETE -> tierData.filter { it.quantity <= 0 }
            PrimeFilter.INCOMPLETE -> tierData.filter { it.quantity > 0 }
            else -> tierData
        }
        if (searchText.isNotEmpty()) {
            tierData = tierData.filter { it.name.contains(searchText, true) }
        }
        return tierData.sortedWith(compareBy({ it.vaulted }, { it.name }))
    }

    private fun searchList(context: Context): List<String> {
        val remainingList: MutableList<String> = mutableListOf()
        for (primeSet in PrimeSetData(context).getListSetData()) {
            for (primeItem in primeSet.primeItems) {
                remainingList.addAll(formatSearchText(primeItem))
            }
        }
        for (primeItem in OtherPrimeData(context).getListOtherData()) {
            remainingList.addAll(formatSearchText(primeItem))
        }
        return remainingList
    }

    private fun formatSearchText(primeItem: PrimeItem): List<String> {
        val remainingList: MutableList<String> = mutableListOf()
        if (!primeItem.blueprint) {
            val itemComp =
                if (primeItem.name == "Kavasa") {
                    "Kubrow Collar BLUEPRINT"
                } else {
                    "BLUEPRINT"
                }
            val searchText = primeItem.name.plus(" Prime ").plus(itemComp)
            remainingList.add(searchText)
        }
        for (component in primeItem.components) {
            if (!component.obtained) {
                val itemComp = when (component.part) {
                    ItemPart.CIRCUIT -> ItemPart.SYSTEMS.toString()
                    ItemPart.ULIMB -> "UPPER LIMB"
                    ItemPart.LLIMB -> "LOWER LIMB"
                    else -> component.part.toString()
                }
                val searchText = primeItem.name.plus(" Prime ").plus(itemComp)
                remainingList.add(searchText)
            }
        }
        return remainingList
    }

    fun getImageTier(relicTier: RelicTier): Int {
        return when (relicTier) {
            RelicTier.Lith -> R.drawable.ic_lith_relic
            RelicTier.Meso -> R.drawable.ic_meso_relic
            RelicTier.Neo -> R.drawable.ic_neo_relic
            RelicTier.Axi -> R.drawable.ic_axi_relic
        }
    }

    fun getRelic(relicName: String): ApiData {
        return apiRelic.getRelicData().first {
            it.name.contains(relicName, true)
        }
    }

    fun getColor(chance: Float): Color {
        return when (chance) {
            25.33f -> Common
            11f -> Uncommon
            2f -> Rare
            else -> Common
        }
    }

    fun formatRelicItemReward(context: Context, name: String): String {
        val textWords = name.split(" ")
        val itemName = StringBuilder()
        if (textWords.contains("Prime")) {
            for (words in textWords) {
                if (words.contains("Prime")) {
                    break
                } else {
                    itemName.append(words).append(" ")
                }
            }
        }

        if (itemName.toString().isNotEmpty()) {
            val hasBlueprint = textWords.contains("Blueprint")
            val compName = if (textWords.contains("Blueprint")) {
                textWords[textWords.size - 2]
            } else {
                if (textWords.contains("Limb")) {
                    textWords[textWords.size - 2] + " " + textWords[textWords.size - 1]
                } else {
                    textWords[textWords.size - 1]
                }
            }
            return context.getString(
                R.string.relic_item_template,
                itemName.toString().substring(0, itemName.lastIndexOf(" ")),
                translateComponent(context, compName),
                if (hasBlueprint) {
                    "(${context.getString(R.string.comp_blueprint)})"
                } else {
                    ""
                }
            )
        } else {
            return context.getString(R.string.forma_blueprint)
        }
    }
}