package com.felipimatheuz.primehunt.util

import android.content.Context
import android.widget.TextView
import com.felipimatheuz.primehunt.model.core.ItemComponent
import com.felipimatheuz.primehunt.model.core.ItemPart
import com.felipimatheuz.primehunt.model.core.PrimeItem
import com.felipimatheuz.primehunt.model.core.PrimeType
import com.felipimatheuz.primehunt.model.external.PrimeRelicApi
import com.felipimatheuz.primehunt.model.external.PrimeSetApi
import com.felipimatheuz.primehunt.model.resources.OtherPrimeData

var apiRelic: PrimeRelicApi = PrimeRelicApi()

var apiData: PrimeSetApi = PrimeSetApi()

fun togglePrimeItem(context: Context, primeItem: PrimeItem) {
    OtherPrimeData(context).togglePrimeItem(primeItem)
}

fun togglePrimeItemComp(primeItem: PrimeItem, textView: TextView, primeItemComp: ItemComponent?) {
    OtherPrimeData(textView.context).togglePrimeItemComp(primeItem, primeItemComp)
}

var otherData: List<PrimeItem> = listOf(
    PrimeItem("Akbronco",
        PrimeType.SECONDARY,
        listOf(
            ItemComponent(ItemPart.BRONCO),
            ItemComponent(ItemPart.BRONCO),
            ItemComponent(ItemPart.LINK)
        )
    ),
    PrimeItem("Aklex",
        PrimeType.SECONDARY,
        listOf(
            ItemComponent(ItemPart.LEX),
            ItemComponent(ItemPart.LEX),
            ItemComponent(ItemPart.LINK)
        )
    ),
    PrimeItem("Akvasto",
        PrimeType.SECONDARY,
        listOf(
            ItemComponent(ItemPart.VASTO),
            ItemComponent(ItemPart.VASTO),
            ItemComponent(ItemPart.LINK)
        )
    ),
    PrimeItem("Braton",
        PrimeType.PRIMARY,
        listOf(
            ItemComponent(ItemPart.BARREL),
            ItemComponent(ItemPart.RECEIVER),
            ItemComponent(ItemPart.STOCK)
        )
    ),
    PrimeItem("Bronco",
        PrimeType.SECONDARY,
        listOf(
            ItemComponent(ItemPart.BARREL),
            ItemComponent(ItemPart.RECEIVER)
        )
    ),
    PrimeItem("Burston",
        PrimeType.PRIMARY,
        listOf(
            ItemComponent(ItemPart.BARREL),
            ItemComponent(ItemPart.RECEIVER),
            ItemComponent(ItemPart.STOCK)
        )
    ),
    PrimeItem("Fang",
        PrimeType.MELEE,
        listOf(
            ItemComponent(ItemPart.BLADE),
            ItemComponent(ItemPart.BLADE),
            ItemComponent(ItemPart.HANDLE),
            ItemComponent(ItemPart.HANDLE)
        )
    ),
    PrimeItem("Lex",
        PrimeType.SECONDARY,
        listOf(
            ItemComponent(ItemPart.BARREL),
            ItemComponent(ItemPart.RECEIVER)
        )
    ),
    PrimeItem("Orthos",
        PrimeType.MELEE,
        listOf(
            ItemComponent(ItemPart.BLADE),
            ItemComponent(ItemPart.BLADE),
            ItemComponent(ItemPart.HANDLE)
        )
    ),
    PrimeItem("Paris",
        PrimeType.PRIMARY,
        listOf(
            ItemComponent(ItemPart.ULIMB),
            ItemComponent(ItemPart.LLIMB),
            ItemComponent(ItemPart.GRIP),
            ItemComponent(ItemPart.STRING)
        )
    ),
    PrimeItem("Vasto",
        PrimeType.SECONDARY,
        listOf(
            ItemComponent(ItemPart.BARREL),
            ItemComponent(ItemPart.RECEIVER)
        )
    )
)