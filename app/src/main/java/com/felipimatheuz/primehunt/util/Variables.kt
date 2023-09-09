package com.felipimatheuz.primehunt.util

import android.content.Context
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.felipimatheuz.primehunt.model.resources.OtherPrimeData
import com.felipimatheuz.primehunt.model.external.PrimeRelicApi
import com.felipimatheuz.primehunt.model.external.PrimeSetApi
import com.felipimatheuz.primehunt.model.resources.PrimeSetData
import com.felipimatheuz.primehunt.model.core.ItemComponent
import com.felipimatheuz.primehunt.model.core.ItemPart
import com.felipimatheuz.primehunt.model.core.PrimeItem

var apiRelic: PrimeRelicApi = PrimeRelicApi()

var apiData: PrimeSetApi = PrimeSetApi()

fun togglePrimeItem(primeSetName: String, textView: TextView, primeItem: PrimeItem, part: ItemPart?) {
    PrimeSetData(textView.context).togglePrimeItem(primeSetName, primeItem, part)
}

fun togglePrimeItem(context: Context, primeItem: PrimeItem) {
    OtherPrimeData(context).togglePrimeItem(primeItem)
}

fun togglePrimeItemComp(primeItem: PrimeItem, textView: TextView, primeItemComp: ItemComponent?) {
    OtherPrimeData(textView.context).togglePrimeItemComp(primeItem, primeItemComp)
}

var otherData: List<PrimeItem> = listOf(
    PrimeItem("Akbronco",
        listOf(
            ItemComponent(ItemPart.BRONCO),
            ItemComponent(ItemPart.BRONCO),
            ItemComponent(ItemPart.LINK)
        )
    ),
    PrimeItem("Aklex",
        listOf(
            ItemComponent(ItemPart.LEX),
            ItemComponent(ItemPart.LEX),
            ItemComponent(ItemPart.LINK)
        )
    ),
    PrimeItem("Akvasto",
        listOf(
            ItemComponent(ItemPart.VASTO),
            ItemComponent(ItemPart.VASTO),
            ItemComponent(ItemPart.LINK)
        )
    ),
    PrimeItem("Braton",
        listOf(
            ItemComponent(ItemPart.BARREL),
            ItemComponent(ItemPart.RECEIVER),
            ItemComponent(ItemPart.STOCK)
        )
    ),
    PrimeItem("Bronco",
        listOf(
            ItemComponent(ItemPart.BARREL),
            ItemComponent(ItemPart.RECEIVER)
        )
    ),
    PrimeItem("Burston",
        listOf(
            ItemComponent(ItemPart.BARREL),
            ItemComponent(ItemPart.RECEIVER),
            ItemComponent(ItemPart.STOCK)
        )
    ),
    PrimeItem("Fang",
        listOf(
            ItemComponent(ItemPart.BLADE),
            ItemComponent(ItemPart.BLADE),
            ItemComponent(ItemPart.HANDLE),
            ItemComponent(ItemPart.HANDLE)
        )
    ),
    PrimeItem("Lex",
        listOf(
            ItemComponent(ItemPart.BARREL),
            ItemComponent(ItemPart.RECEIVER)
        )
    ),
    PrimeItem("Orthos",
        listOf(
            ItemComponent(ItemPart.BLADE),
            ItemComponent(ItemPart.BLADE),
            ItemComponent(ItemPart.HANDLE)
        )
    ),
    PrimeItem("Paris",
        listOf(
            ItemComponent(ItemPart.ULIMB),
            ItemComponent(ItemPart.LLIMB),
            ItemComponent(ItemPart.GRIP),
            ItemComponent(ItemPart.STRING)
        )
    ),
    PrimeItem("Vasto",
        listOf(
            ItemComponent(ItemPart.BARREL),
            ItemComponent(ItemPart.RECEIVER)
        )
    )
)