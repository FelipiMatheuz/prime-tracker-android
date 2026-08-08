package com.felipimatheuz.primehunt.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.local.entity.RelicEntity
import com.felipimatheuz.primehunt.domain.model.enums.RelicEra
import com.felipimatheuz.primehunt.domain.model.enums.RelicSource
import kotlinx.coroutines.flow.Flow

@Dao
interface RelicDao {

    @Query("""
        SELECT *
        FROM relic
        ORDER BY era, name
    """)
    fun getAll(): Flow<List<RelicEntity>>

    @Query("""
        SELECT *
        FROM relic
        WHERE id = :id
    """)
    suspend fun getById(id: String): RelicEntity?

    @Query("""
        SELECT *
        FROM relic
        WHERE era = :era
        ORDER BY name
    """)
    fun getByEra(era: RelicEra): Flow<List<RelicEntity>>

    @Query("""
        SELECT *
        FROM relic
        WHERE source = :source
        ORDER BY era, name
    """)
    fun getBySource(source: RelicSource): Flow<List<RelicEntity>>

    @Query("""
        SELECT *
        FROM relic
        WHERE name LIKE '%' || :query || '%'
        ORDER BY era, name
    """)
    fun search(query: String): Flow<List<RelicEntity>>

    @Query("SELECT COUNT(*) FROM relic")
    fun count(): Flow<Int>

    @Query("SELECT COUNT(*) FROM relic WHERE source = :source")
    fun countBySource(source: RelicSource): Flow<Int>

    @Upsert
    suspend fun upsertAll(items: List<RelicEntity>)
}