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

val offlinePrimeSet = """
    [
        {
            "imgLink": "https://warframe-web-assets.nyc3.cdn.digitaloceanspaces.com/uploads/thumbnails/81e05bfbd34036ef9c86e86fe70611e3_1600x900.png",
            "status": "ACTIVE",
            "released": 20230727,
            "setName": "Wisp",
            "primeItems": [
                {
                    "name": "Wisp",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Fulmin",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Gunsen",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/thumbnails/8260dab07e051d35bcef1a2a82c22965_1600x900.png",
            "status": "ACTIVE",
            "released": 20230315,
            "setName": "Hildryn",
            "primeItems": [
                {
                    "name": "Hildryn",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Larkspur",
                    "type": "OTHER",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Shade",
                    "type": "OTHER",
                    "components": [
                        {
                            "part": "CARAPACE"
                        },
                        {
                            "part": "CEREBRUM"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/thumbnails/17806948a87f33e6e6f93f81ab6dc125_1600x900.png",
            "status": "ACTIVE",
            "released": 20221214,
            "setName": "Baruuk",
            "primeItems": [
                {
                    "name": "Baruuk",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Afuris",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        },
                        {
                            "part": "RECEIVER"
                        },
                        {
                            "part": "LINK"
                        }
                    ]
                },
                {
                    "name": "Cobra & Crane",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HILT"
                        },
                        {
                            "part": "GUARD"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/thumbnails/eb94e8216684486f2d8254fac05cbac9_1600x900.png",
            "status": "ACTIVE",
            "released": 20221005,
            "setName": "Revenant",
            "primeItems": [
                {
                    "name": "Revenant",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Phantasma",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Tatsu",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/4647358c56474d627593ad56ab2d2d38.png",
            "status": "ACTIVE",
            "released": 20220716,
            "setName": "Khora",
            "primeItems": [
                {
                    "name": "Khora",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Hystrix",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Dual Keres",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/d60c5791a323aaeafba03714603474b3.jpg",
            "status": "ACTIVE",
            "released": 20220328,
            "setName": "Garuda",
            "primeItems": [
                {
                    "name": "Garuda",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Nagantaka",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Corvas",
                    "type": "OTHER",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/c21bd43a642175d6f18ccbbf191b43ee.jpg",
            "status": "ACTIVE",
            "released": 20211215,
            "setName": "Harrow",
            "primeItems": [
                {
                    "name": "Harrow",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Scourge",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                },
                {
                    "name": "Knell",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/38d4625182cf8d130e05481ede5b507d.png",
            "status": "VAULT",
            "released": 20210908,
            "setName": "Nidus",
            "primeItems": [
                {
                    "name": "Nidus",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Strun",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Magnus",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/bbe88a08b1c65966b5b023709ee62ec5.jpg",
            "status": "VAULT",
            "released": 20210525,
            "setName": "Gara",
            "primeItems": [
                {
                    "name": "Gara",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Astilla",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Volnus",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "HEAD"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/f11fbecd9eb92f869d185b710cbd79fd.png",
            "status": "VAULT",
            "released": 20210223,
            "setName": "Octavia",
            "primeItems": [
                {
                    "name": "Octavia",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Tenora",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Pandero",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://images2.minutemediacdn.com/image/fetch/w_2000,h_2000,c_fit/https%3A%2F%2Fapptrigger.com%2Ffiles%2F2020%2F10%2FNezha_Prime.jpg",
            "status": "VAULT",
            "released": 20201027,
            "setName": "Nezha",
            "primeItems": [
                {
                    "name": "Nezha",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Zakti",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Guandao",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/thumbnails/85451e0021c2734b7b720bf33e8641f2_1600x900.png",
            "status": "VAULT",
            "released": 20200707,
            "setName": "Inaros",
            "primeItems": [
                {
                    "name": "Inaros",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Panthera",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Karyst",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/16d278cd8b6a4c2ca51115874d9269fc.jpg",
            "status": "VAULT",
            "released": 20200331,
            "setName": "Titania",
            "primeItems": [
                {
                    "name": "Titania",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Corinth",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Pangolin",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/b5edcc1223c5ad95d87145e102a2afa2.png",
            "status": "VAULT",
            "released": 20191217,
            "setName": "Ivara",
            "primeItems": [
                {
                    "name": "Ivara",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Baza",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Aksomati",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        },
                        {
                            "part": "RECEIVER"
                        },
                        {
                            "part": "LINK"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/thumbnails/b25383db35a9b4f259231e2d91211a9a_1600x900.png",
            "status": "VAULT",
            "released": 20191001,
            "setName": "Atlas",
            "primeItems": [
                {
                    "name": "Atlas",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Tekko",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "GAUNTLET"
                        },
                        {
                            "part": "GAUNTLET"
                        }
                    ]
                },
                {
                    "name": "Dethcube",
                    "type": "OTHER",
                    "components": [
                        {
                            "part": "CARAPACE"
                        },
                        {
                            "part": "CEREBRUM"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/4d57da42c98a161620c793727bfe1584.jpg",
            "status": "RESURGENCE",
            "released": 20190706,
            "setName": "Wukong",
            "primeItems": [
                {
                    "name": "Wukong",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Zhuge",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "GRIP"
                        },
                        {
                            "part": "STRING"
                        },
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Ninkondi",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "HANDLE"
                        },
                        {
                            "part": "HANDLE"
                        },
                        {
                            "part": "CHAIN"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/e119dbb56e99bca2a1c27bbff9852818.jpg",
            "status": "RESURGENCE",
            "released": 20190402,
            "setName": "Equinox",
            "primeItems": [
                {
                    "name": "Equinox",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Stradavar",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Tipedo",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "HANDLE"
                        },
                        {
                            "part": "HANDLE"
                        },
                        {
                            "part": "ORNAMENT"
                        },
                        {
                            "part": "ORNAMENT"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/e66edbe6644f874025ceaebf65c6cf36.jpg",
            "status": "VAULT",
            "released": 20181218,
            "setName": "Mesa",
            "primeItems": [
                {
                    "name": "Mesa",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Akjagara",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        },
                        {
                            "part": "RECEIVER"
                        },
                        {
                            "part": "LINK"
                        }
                    ]
                },
                {
                    "name": "Redeemer",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/15f6ae2f16b9a8141591d42210fcf86e.jpeg",
            "status": "VAULT",
            "released": 20180925,
            "setName": "Chroma",
            "primeItems": [
                {
                    "name": "Chroma",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Rubico",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Gram",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/16bafe11f0a19c9619e96e3a890728ec.jpg",
            "status": "VAULT",
            "released": 20180619,
            "setName": "Limbo",
            "primeItems": [
                {
                    "name": "Limbo",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Pyrana",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Destreza",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/3ecd12cdc3ddfa048e8a9a702648b4f2.jpeg",
            "status": "VAULT",
            "released": 20180320,
            "setName": "Zephyr",
            "primeItems": [
                {
                    "name": "Zephyr",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Tiberon",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Kronen",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/30234e9c1dfffdd5c4583fe2a6788d97.png",
            "status": "VAULT",
            "released": 20171212,
            "setName": "Mirage",
            "primeItems": [
                {
                    "name": "Mirage",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Akbolto",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        },
                        {
                            "part": "RECEIVER"
                        },
                        {
                            "part": "LINK"
                        }
                    ]
                },
                {
                    "name": "Kogake",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "GAUNTLET"
                        },
                        {
                            "part": "GAUNTLET"
                        },
                        {
                            "part": "BOOT"
                        },
                        {
                            "part": "BOOT"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/thumbnails/39aa3c8cd8760e232b927b1d8c4cb151_1600x900.png",
            "status": "VAULT",
            "released": 20170829,
            "setName": "Hydroid",
            "primeItems": [
                {
                    "name": "Hydroid",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Ballistica",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "ULIMB"
                        },
                        {
                            "part": "LLIMB"
                        },
                        {
                            "part": "STRING"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Nami Skyla",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/a3383c407d485c30d724267f374a3438.jpg",
            "status": "VAULT",
            "released": 20170530,
            "setName": "Oberon",
            "primeItems": [
                {
                    "name": "Oberon",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Sybaris",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Silva & Aegis",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "GUARD"
                        },
                        {
                            "part": "HILT"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/1d7084c14e3ed054951b685a59961248.png",
            "status": "VAULT",
            "released": 20170228,
            "setName": "Banshee",
            "primeItems": [
                {
                    "name": "Banshee",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Euphona",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Helios",
                    "type": "OTHER",
                    "components": [
                        {
                            "part": "CARAPACE"
                        },
                        {
                            "part": "CEREBRUM"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/305a39a05e4abe141e82cba89534520b.jpg",
            "status": "ACTIVE",
            "released": 20161122,
            "setName": "Valkyr",
            "primeItems": [
                {
                    "name": "Valkyr",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Cernos",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "ULIMB"
                        },
                        {
                            "part": "LLIMB"
                        },
                        {
                            "part": "GRIP"
                        },
                        {
                            "part": "STRING"
                        }
                    ]
                },
                {
                    "name": "Venka",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "GAUNTLET"
                        },
                        {
                            "part": "GAUNTLET"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/b3c8bf8903b3b56257a858174bc4a718.jpg",
            "status": "VAULT",
            "released": 20160823,
            "setName": "Nekros",
            "primeItems": [
                {
                    "name": "Nekros",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Tigris",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Galatine",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/7d7b8ccfd4c63792249711adfb8a0665.jpg",
            "status": "VAULT",
            "released": 20160517,
            "setName": "Vauban",
            "primeItems": [
                {
                    "name": "Vauban",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Fragor",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "HEAD"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                },
                {
                    "name": "Akstiletto",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        },
                        {
                            "part": "RECEIVER"
                        },
                        {
                            "part": "LINK"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/40e4c8a4d27711c44ad0c11ef853857f.jpg",
            "status": "VAULT",
            "released": 20160216,
            "setName": "Saryn",
            "primeItems": [
                {
                    "name": "Saryn",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Nikana",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HILT"
                        }
                    ]
                },
                {
                    "name": "Spira",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "POUCH"
                        },
                        {
                            "part": "POUCH"
                        },
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "BLADE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/671f4275d86e6d3de79d7b85e1aefda9.jpg",
            "status": "VAULT",
            "released": 20151006,
            "setName": "Trinity",
            "primeItems": [
                {
                    "name": "Trinity",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Dual Kamas",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                },
                {
                    "name": "Kavasa",
                    "type": "OTHER",
                    "components": [
                        {
                            "part": "BAND"
                        },
                        {
                            "part": "BUCKLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/471fc4cbafe6d6b96db4fd29c0fa585d.jpg",
            "status": "VAULT",
            "released": 20150707,
            "setName": "Ash",
            "primeItems": [
                {
                    "name": "Ash",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Vectis",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Carrier",
                    "type": "OTHER",
                    "components": [
                        {
                            "part": "CARAPACE"
                        },
                        {
                            "part": "CEREBRUM"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://content.invisioncic.com/Mwarframe/monthly_2019_04/PrimeVault_Volt_Keyart_1080p_V02.jpg.98623cdd291eb64928a634c796f88f4b.jpg",
            "status": "BARO",
            "released": 20150324,
            "setName": "Volt",
            "primeItems": [
                {
                    "name": "Volt",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Odonata",
                    "type": "OTHER",
                    "components": [
                        {
                            "part": "HARNESS"
                        },
                        {
                            "part": "CIRCUIT"
                        },
                        {
                            "part": "WINGS"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/5cfd1308a2da381c5fd674d68a203023.jpg",
            "status": "VAULT",
            "released": 20141216,
            "setName": "Nova",
            "primeItems": [
                {
                    "name": "Nova",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Soma",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        },
                        {
                            "part": "STOCK"
                        }
                    ]
                },
                {
                    "name": "Vasto",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/082f4873c201c1448760704a388778f8.jpg",
            "status": "ACTIVE",
            "released": 20140923,
            "setName": "Nyx",
            "primeItems": [
                {
                    "name": "Nyx",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Hikou",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "POUCH"
                        },
                        {
                            "part": "POUCH"
                        },
                        {
                            "part": "STARS"
                        },
                        {
                            "part": "STARS"
                        }
                    ]
                },
                {
                    "name": "Scindo",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://content.invisioncic.com/Mwarframe/monthly_2019_04/PrimeVault_Loki_Keyart_1080p_V04.jpg.df2f8115ed746aad6af78a68e5f698f5.jpg",
            "status": "VAULT",
            "released": 20140611,
            "setName": "Loki",
            "primeItems": [
                {
                    "name": "Loki",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Bo",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "ORNAMENT"
                        },
                        {
                            "part": "ORNAMENT"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                },
                {
                    "name": "Wyrm",
                    "type": "OTHER",
                    "components": [
                        {
                            "part": "CARAPACE"
                        },
                        {
                            "part": "CEREBRUM"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/873e13865b2670238a5512eb9d90c80c.jpg",
            "status": "VAULT",
            "released": 20140305,
            "setName": "Rhino",
            "primeItems": [
                {
                    "name": "Rhino",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Boltor",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Ankyros",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "GAUNTLET"
                        },
                        {
                            "part": "GAUNTLET"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/ca60f023d8977cdf8fe496bb104865c8.jpg",
            "status": "VAULT",
            "released": 20131120,
            "setName": "Ember",
            "primeItems": [
                {
                    "name": "Ember",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Sicarus",
                    "type": "SECONDARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Glaive",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "DISC"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/51adfdf2aae9f169cd05686f2759b52d.jpg",
            "status": "VAULT",
            "released": 20130913,
            "setName": "Mag",
            "primeItems": [
                {
                    "name": "Mag",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Boar",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Dakra",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        },
        {
            "imgLink": "https://n9e5v4d8.ssl.hwcdn.net/uploads/96871ec2d48ac795a0c690e94a672e9b.jpg",
            "status": "VAULT",
            "released": 20130503,
            "setName": "Frost",
            "primeItems": [
                {
                    "name": "Frost",
                    "type": "WARFRAME",
                    "components": [
                        {
                            "part": "NEUROPTICS"
                        },
                        {
                            "part": "CHASSIS"
                        },
                        {
                            "part": "SYSTEMS"
                        }
                    ]
                },
                {
                    "name": "Latron",
                    "type": "PRIMARY",
                    "components": [
                        {
                            "part": "BARREL"
                        },
                        {
                            "part": "STOCK"
                        },
                        {
                            "part": "RECEIVER"
                        }
                    ]
                },
                {
                    "name": "Reaper",
                    "type": "MELEE",
                    "components": [
                        {
                            "part": "BLADE"
                        },
                        {
                            "part": "HANDLE"
                        }
                    ]
                }
            ]
        }
    ]
""".trimIndent()

val offlineOtherPrimes = """
    [
        {
            "name": "Akbronco",
            "type": "SECONDARY",
            "components": [
                {
                    "part": "BRONCO"
                },
                {
                    "part": "BRONCO"
                },
                {
                    "part": "LINK"
                }
            ]
        },
        {
            "name": "Aklex",
            "type": "SECONDARY",
            "components": [
                {
                    "part": "LEX"
                },
                {
                    "part": "LEX"
                },
                {
                    "part": "LINK"
                }
            ]
        },
        {
            "name": "Akvasto",
            "type": "SECONDARY",
            "components": [
                {
                    "part": "VASTO"
                },
                {
                    "part": "VASTO"
                },
                {
                    "part": "LINK"
                }
            ]
        },
        {
            "name": "Braton",
            "type": "PRIMARY",
            "components": [
                {
                    "part": "BARREL"
                },
                {
                    "part": "STOCK"
                },
                {
                    "part": "RECEIVER"
                }
            ]
        },
        {
            "name": "Bronco",
            "type": "SECONDARY",
            "components": [
                {
                    "part": "BARREL"
                },
                {
                    "part": "RECEIVER"
                }
            ]
        },
        {
            "name": "Burston",
            "type": "PRIMARY",
            "components": [
                {
                    "part": "BARREL"
                },
                {
                    "part": "STOCK"
                },
                {
                    "part": "RECEIVER"
                }
            ]
        },
        {
            "name": "Fang",
            "type": "MELEE",
            "components": [
                {
                    "part": "BLADE"
                },
                {
                    "part": "BLADE"
                },
                {
                    "part": "HANDLE"
                },
                {
                    "part": "HANDLE"
                }
            ]
        },
        {
            "name": "Lex",
            "type": "SECONDARY",
            "components": [
                {
                    "part": "BARREL"
                },
                {
                    "part": "RECEIVER"
                }
            ]
        },
        {
            "name": "Orthos",
            "type": "MELEE",
            "components": [
                {
                    "part": "BLADE"
                },
                {
                    "part": "BLADE"
                },
                {
                    "part": "HANDLE"
                }
            ]
        },
        {
            "name": "Paris",
            "type": "PRIMARY",
            "components": [
                {
                    "part": "ULIMB"
                },
                {
                    "part": "LLIMB"
                },
                {
                    "part": "GRIP"
                },
                {
                    "part": "STRING"
                }
            ]
        },
        {
            "name": "Vasto",
            "type": "SECONDARY",
            "components": [
                {
                    "part": "BARREL"
                },
                {
                    "part": "RECEIVER"
                }
            ]
        }
    ]
""".trimIndent()
