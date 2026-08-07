package com.felipimatheuz.primehunt.data.cloud

import androidx.annotation.Keep
import com.felipimatheuz.primehunt.ui.viewmodel.cloud.CloudActionResult
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class Firestore @Inject constructor() {

    @Keep
    data class CloudBackup(
        val inventory: Map<String, Int> = emptyMap(),
        val goals: List<Map<String, Any?>> = emptyList(),
        val tags: List<Map<String, Any?>> = emptyList(),
        val updatedAt: Long = 0L
    )

    suspend fun uploadBackup(userId: String, inventory: Map<String, Int>, goals: List<Map<String, Any?>>, tags: List<Map<String, Any?>>): CloudActionResult {
        val data = CloudBackup(
            inventory = inventory,
            goals = goals,
            tags = tags,
            updatedAt = System.currentTimeMillis()
        )
        return try {
            Firebase.firestore.collection("backups").document(userId)
                .set(data)
                .await()
            CloudActionResult.SuccessBackupUpload
        } catch (e: Exception) {
            CloudActionResult.Error(e.message ?: "Unknown upload error")
        }
    }

    suspend fun downloadBackup(userId: String): BackupResult {
        return try {
            val result = Firebase.firestore.collection("backups").document(userId).get().await()
            if (result.exists()) {
                val backup = result.toObject(CloudBackup::class.java)
                if (backup != null) {
                    BackupResult.Success(backup.inventory, backup.goals, backup.tags)
                } else {
                    BackupResult.Error("Failed to parse backup data")
                }
            } else {
                BackupResult.Error("No backup found")
            }
        } catch (e: Exception) {
            BackupResult.Error(e.message ?: "Unknown download error")
        }
    }

    suspend fun deleteCloudData(userId: String): CloudActionResult {
        return try {
            Firebase.firestore.collection("backups").document(userId).delete().await()
            CloudActionResult.SuccessClearData
        } catch (e: Exception) {
            CloudActionResult.Error(e.message ?: "Unknown deletion error")
        }
    }

    suspend fun readLegacyChecklist(userId: String): LegacyChecklistResult {
        return try {
            val result = Firebase.firestore.collection("checklist").document(userId).get().await()
            if (result.exists()) {
                LegacyChecklistResult.Success(result.data ?: emptyMap())
            } else {
                LegacyChecklistResult.Error("Nothing to migrate")
            }
        } catch (e: Exception) {
            LegacyChecklistResult.Error(e.message ?: "Unknown migration read error")
        }
    }

    suspend fun deleteLegacyChecklist(userId: String): Boolean {
        return try {
            Firebase.firestore.collection("checklist").document(userId).delete().await()
            true
        } catch (_: Exception) {
            false
        }
    }

    sealed class LegacyChecklistResult {
        data class Success(val data: Map<String, Any>) : LegacyChecklistResult()
        data class Error(val message: String) : LegacyChecklistResult()
    }

    sealed class BackupResult {
        data class Success(val inventory: Map<String, Int>, val goals: List<Map<String, Any?>>, val tags: List<Map<String, Any?>>) : BackupResult()
        data class Error(val message: String) : BackupResult()
    }
}
