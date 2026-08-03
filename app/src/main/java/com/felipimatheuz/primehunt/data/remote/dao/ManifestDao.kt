package com.felipimatheuz.primehunt.data.remote.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.felipimatheuz.primehunt.data.remote.entity.LocalManifest
import kotlinx.coroutines.flow.Flow

@Dao
interface ManifestDao {

    @Query("SELECT * FROM manifest WHERE id = 1")
    fun observeManifest(): Flow<LocalManifest?>

    @Query("SELECT * FROM manifest WHERE id = 1")
    suspend fun getManifest(): LocalManifest?

    @Upsert
    suspend fun upsert(manifest: LocalManifest)
}
