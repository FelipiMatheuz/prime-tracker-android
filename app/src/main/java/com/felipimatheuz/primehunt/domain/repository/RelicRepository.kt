package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.domain.model.RelicDomain
import kotlinx.coroutines.flow.Flow

interface RelicRepository {
    fun observeAllRelics(): Flow<List<RelicDomain>>
    fun getRelicCounts(): Flow<RelicCounts>
}

data class RelicCounts(
    val available: Int,
    val vaulted: Int,
    val resurgence: Int,
    val baro: Int
)
