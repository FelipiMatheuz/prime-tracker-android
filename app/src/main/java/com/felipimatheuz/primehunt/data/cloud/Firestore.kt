package com.felipimatheuz.primehunt.data.cloud

import com.felipimatheuz.primehunt.domain.repository.CloudRemoteDataSource
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Firestore @Inject constructor() : CloudRemoteDataSource {

    override suspend fun uploadBackup(userId: String, data: Map<String, Any?>): Boolean {
        return try {
            Firebase.firestore.collection("backups").document(userId)
                .set(data)
                .await()
            true
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun downloadBackup(userId: String): Map<String, Any?>? {
        return try {
            val result = Firebase.firestore.collection("backups").document(userId).get().await()
            if (result.exists()) {
                result.data
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun deleteBackup(userId: String): Boolean {
        return try {
            Firebase.firestore.collection("backups").document(userId).delete().await()
            true
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun readLegacyChecklist(userId: String): CloudRemoteDataSource.LegacyChecklistResult {
        return try {
            val result = Firebase.firestore.collection("checklist").document(userId).get().await()
            if (result.exists()) {
                CloudRemoteDataSource.LegacyChecklistResult.Success(result.data ?: emptyMap())
            } else {
                CloudRemoteDataSource.LegacyChecklistResult.Error("Nothing to migrate")
            }
        } catch (e: Exception) {
            CloudRemoteDataSource.LegacyChecklistResult.Error(e.message ?: "Unknown migration read error")
        }
    }

    override suspend fun deleteLegacyChecklist(userId: String): Boolean {
        return try {
            Firebase.firestore.collection("checklist").document(userId).delete().await()
            true
        } catch (_: Exception) {
            false
        }
    }
}
