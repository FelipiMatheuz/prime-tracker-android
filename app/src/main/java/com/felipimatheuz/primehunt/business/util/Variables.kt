package com.felipimatheuz.primehunt.business.util

import com.felipimatheuz.primehunt.model.RelicItem
import com.felipimatheuz.primehunt.model.Reward
import com.felipimatheuz.primehunt.business.external.OtherPrimeApi
import com.felipimatheuz.primehunt.business.external.PrimeRelicApi
import com.felipimatheuz.primehunt.business.external.PrimeSetApi

lateinit var apiRelic: PrimeRelicApi

lateinit var apiSet: PrimeSetApi

lateinit var apiOther: OtherPrimeApi

val urlPrimeItem = mapOf(
    "Akbronco" to "https://static.wikia.nocookie.net/warframe/images/1/1c/AkbroncoPrime.png/revision/latest?cb=20220407125704",
    "Aklex" to "https://static.wikia.nocookie.net/warframe/images/a/ab/AklexPrime.png/revision/latest?cb=20220407125931",
    "Akmagnus" to "https://static.wikia.nocookie.net/warframe/images/1/11/AkmagnusPrime.png/revision/latest?cb=20240726202455",
    "Akvasto" to "https://static.wikia.nocookie.net/warframe/images/0/03/AkvastoPrime.png/revision/latest?cb=20220407130244",
    "Braton" to "https://static.wikia.nocookie.net/warframe/images/2/2e/BratonPrime.png/revision/latest?cb=20220407163420",
    "Bronco" to "https://static.wikia.nocookie.net/warframe/images/3/37/BroncoPrime.png/revision/latest?cb=20220407163629",
    "Burston" to "https://static.wikia.nocookie.net/warframe/images/a/a4/BurstonPrime.png/revision/latest?cb=20220407163806",
    "Fang" to "https://static.wikia.nocookie.net/warframe/images/f/f7/FangPrime.png/revision/latest?cb=20220410002159",
    "Lex" to "https://static.wikia.nocookie.net/warframe/images/9/9a/LexPrime.png/revision/latest?cb=20220421015247",
    "Orthos" to "https://static.wikia.nocookie.net/warframe/images/4/4a/OrthosPrime.png/revision/latest?cb=20220612022152",
    "Paris" to "https://static.wikia.nocookie.net/warframe/images/8/86/ParisPrime.png/revision/latest?cb=20220612022519",
    "Vasto" to "https://static.wikia.nocookie.net/warframe/images/a/aa/VastoPrime.png/revision/latest?cb=20220612123902"
)

val missingRewardList = mapOf(
    "Lith L4" to listOf(
        Reward("Rare", 2f, RelicItem("Latron Prime Blueprint")),
        Reward("Uncommon", 11f, RelicItem("Reaper Prime Blueprint")),
        Reward("Uncommon", 11f, RelicItem("Vasto Prime Blueprint")),
        Reward("Common", 25.33f, RelicItem("Boar Prime Receiver")),
        Reward("Common", 25.33f, RelicItem("Mag Prime Systems Blueprint")),
        Reward("Common", 25.33f, RelicItem("Forma Blueprint"))
    ),
    "Lith M8" to listOf(
        Reward("Rare", 2f, RelicItem("Mag Prime Blueprint")),
        Reward("Uncommon", 11f, RelicItem("Frost Prime Systems Blueprint")),
        Reward("Uncommon", 11f, RelicItem("Reaper Prime Blade")),
        Reward("Common", 25.33f, RelicItem("Dakra Prime Handle")),
        Reward("Common", 25.33f, RelicItem("Vasto Prime Barrel")),
        Reward("Common", 25.33f, RelicItem("Forma Blueprint"))
    ),
    "Meso F4" to listOf(
        Reward("Rare", 2f, RelicItem("Frost Prime Blueprint")),
        Reward("Uncommon", 11f, RelicItem("Mag Prime Chassis Blueprint")),
        Reward("Uncommon", 11f, RelicItem("Boar Prime Blueprint")),
        Reward("Common", 25.33f, RelicItem("Reaper Prime Handle")),
        Reward("Common", 25.33f, RelicItem("Latron Prime Stock")),
        Reward("Common", 25.33f, RelicItem("Forma Blueprint"))
    ),
    "Neo B8" to listOf(
        Reward("Rare", 2f, RelicItem("Boar Prime Stock")),
        Reward("Uncommon", 11f, RelicItem("Dakra Prime Blueprint")),
        Reward("Uncommon", 11f, RelicItem("Latron Prime Receiver")),
        Reward("Common", 25.33f, RelicItem("Frost Prime Chassis Blueprint")),
        Reward("Common", 25.33f, RelicItem("Vasto Prime Receiver")),
        Reward("Common", 25.33f, RelicItem("Forma Blueprint"))
    ),
    "Axi D4" to listOf(
        Reward("Rare", 2f, RelicItem("Dakra Prime Blade")),
        Reward("Uncommon", 11f, RelicItem("Boar Prime Barrel")),
        Reward("Uncommon", 11f, RelicItem("Mag Prime Neuroptics Blueprint")),
        Reward("Common", 25.33f, RelicItem("Frost Prime Neuroptics Blueprint")),
        Reward("Common", 25.33f, RelicItem("Latron Prime Barrel")),
        Reward("Common", 25.33f, RelicItem("Forma Blueprint"))
    )
)
