package com.felipimatheuz.primehunt.data.model

import com.felipimatheuz.primehunt.data.local.entity.PrimeComponentEntity
import com.felipimatheuz.primehunt.data.local.entity.PrimePartEntity
import com.felipimatheuz.primehunt.data.local.entity.PrimeSetEntity
import com.felipimatheuz.primehunt.data.local.entity.RelicEntity

data class PrimeBaseData(
    val sets: List<PrimeSetEntity>,
    val parts: List<PrimePartEntity>,
    val components: List<PrimeComponentEntity>,
    val relics: List<RelicEntity>
)
