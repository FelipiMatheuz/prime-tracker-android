package com.felipimatheuz.primehunt.domain.repository

interface CloudRemoteDataSource {
    
    sealed class LegacyChecklistResult {
        data class Success(val data: Map<String, Any>) : LegacyChecklistResult()
        data class Error(val message: String) : LegacyChecklistResult()
    }

    suspend fun readLegacyChecklist(userId: String): LegacyChecklistResult
    suspend fun deleteLegacyChecklist(userId: String): Boolean
    
    suspend fun uploadBackup(userId: String, data: Map<String, Any?>): Boolean
    suspend fun downloadBackup(userId: String): Map<String, Any?>?
    suspend fun deleteBackup(userId: String): Boolean
}
