package com.felipimatheuz.primehunt.data.remote.firebase

import com.felipimatheuz.primehunt.data.remote.dto.AnnouncementDto
import com.felipimatheuz.primehunt.data.remote.dto.ProfileDto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface SquadRemoteDataSource {
    fun observeConnectionState(): Flow<Boolean>
    fun observeServerTimeOffset(): Flow<Long>
    suspend fun saveProfile(profile: ProfileDto): Result<Unit>
    suspend fun getProfile(userId: String): Result<ProfileDto?>
    suspend fun createAnnouncement(announcement: AnnouncementDto, estimatedServerTime: Long): Result<Unit>
    suspend fun heartbeatAnnouncement(userId: String, estimatedServerTime: Long): Result<Unit>
    suspend fun deleteAnnouncement(userId: String): Result<Unit>
    fun observeAnnouncements(): Flow<List<AnnouncementDto>>
}

@Singleton
class FirebaseSquadRemoteDataSource @Inject constructor(
    database: FirebaseDatabase
) : SquadRemoteDataSource {

    private val profilesRef = database.getReference("profiles")
    private val announcementsRef = database.getReference("announcements")
    private val connectedRef = database.getReference(".info/connected")
    private val offsetRef = database.getReference(".info/serverTimeOffset")

    override fun observeConnectionState(): Flow<Boolean> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                trySend(connected)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(false)
            }
        }
        connectedRef.addValueEventListener(listener)
        awaitClose { connectedRef.removeEventListener(listener) }
    }

    override fun observeServerTimeOffset(): Flow<Long> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val offset = snapshot.getValue(Long::class.java) ?: 0L
                trySend(offset)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(0L)
            }
        }
        offsetRef.addValueEventListener(listener)
        awaitClose { offsetRef.removeEventListener(listener) }
    }

    override suspend fun saveProfile(profile: ProfileDto): Result<Unit> = runCatching {
        profilesRef.child(profile.userId).setValue(profile).await()
    }

    override suspend fun getProfile(userId: String): Result<ProfileDto?> = runCatching {
        val snapshot = profilesRef.child(userId).get().await()
        if (snapshot.exists()) {
            snapshot.getValue(ProfileDto::class.java)
        } else {
            null
        }
    }

    override suspend fun createAnnouncement(
        announcement: AnnouncementDto,
        estimatedServerTime: Long
    ): Result<Unit> = runCatching {
        val createdAt = if (announcement.createdAt > 0L) announcement.createdAt else estimatedServerTime
        val expiresAt = estimatedServerTime + EXPIRATION_TTL_MS

        val payload = announcement.copy(
            createdAt = createdAt,
            expiresAt = expiresAt
        )
        announcementsRef.child(payload.userId).setValue(payload).await()
    }

    override suspend fun heartbeatAnnouncement(
        userId: String,
        estimatedServerTime: Long
    ): Result<Unit> = runCatching {
        val newExpiresAt = estimatedServerTime + EXPIRATION_TTL_MS
        announcementsRef.child(userId).child("expiresAt").setValue(newExpiresAt).await()
    }

    override suspend fun deleteAnnouncement(userId: String): Result<Unit> = runCatching {
        announcementsRef.child(userId).removeValue().await()
    }

    override fun observeAnnouncements(): Flow<List<AnnouncementDto>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<AnnouncementDto>()
                for (child in snapshot.children) {
                    child.getValue(AnnouncementDto::class.java)?.let { list.add(it) }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        announcementsRef.addValueEventListener(listener)
        awaitClose { announcementsRef.removeEventListener(listener) }
    }

    companion object {
        const val HEARTBEAT_INTERVAL_MS = 120_000L // 2 minutes
        const val EXPIRATION_TTL_MS = 300_000L     // 5 minutes
    }
}
