package com.felipimatheuz.primehunt.data.remote.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.remote.entity.RelicEntity
import com.felipimatheuz.primehunt.data.remote.enums.RelicEra
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
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

    @Upsert
    suspend fun upsertAll(items: List<RelicEntity>)
}