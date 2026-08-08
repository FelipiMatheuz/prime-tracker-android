package com.felipimatheuz.primehunt.domain.repository

interface CloudRepository {
    data class MigrationResult(
        val itemsInserted: Int,
        val ignoredKeys: List<String>
    )

    suspend fun performMigration(
        mapSet: Map<String, Any>,
        mapOther: Map<String, Any>
    ): MigrationResult

    suspend fun restoreBackup(
        inventoryMap: Map<String, Int>,
        goalsList: List<Map<String, Any?>>,
        tagsList: List<Map<String, Any?>>
    )
}
