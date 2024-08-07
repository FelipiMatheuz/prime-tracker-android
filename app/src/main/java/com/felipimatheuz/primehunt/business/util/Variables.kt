package com.felipimatheuz.primehunt.business.util

import com.felipimatheuz.primehunt.model.*

lateinit var primeSetList: List<PrimeSet>
lateinit var otherPrimeList: List<PrimeItem>
lateinit var relicList: List<RelicSet>

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
