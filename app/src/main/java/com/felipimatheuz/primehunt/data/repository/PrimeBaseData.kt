package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.remote.entity.PrimeComponentEntity
import com.felipimatheuz.primehunt.data.remote.entity.PrimePartEntity
import com.felipimatheuz.primehunt.data.remote.entity.PrimeSetEntity
import com.felipimatheuz.primehunt.data.remote.entity.RelicEntity

data class PrimeBaseData(
    val sets: List<PrimeSetEntity>,
    val parts: List<PrimePartEntity>,
    val components: List<PrimeComponentEntity>,
    val relics: List<RelicEntity>
)
