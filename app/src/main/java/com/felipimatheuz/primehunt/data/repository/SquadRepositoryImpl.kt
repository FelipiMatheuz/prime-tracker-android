package com.felipimatheuz.primehunt.data.repository

import com.felipimatheuz.primehunt.data.cloud.GoogleCredential
import com.felipimatheuz.primehunt.data.mapper.SquadMapper
import com.felipimatheuz.primehunt.data.model.PrimeBaseData
import com.felipimatheuz.primehunt.data.remote.dto.AnnouncementDto
import com.felipimatheuz.primehunt.data.remote.firebase.SquadRemoteDataSource
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.domain.model.squad.SquadAnnouncement
import com.felipimatheuz.primehunt.domain.model.squad.UserProfile
import com.felipimatheuz.primehunt.domain.repository.SquadRepository
import com.felipimatheuz.primehunt.domain.util.StringFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SquadRepositoryImpl @Inject constructor(
    private val remoteDataSource: SquadRemoteDataSource,
    private val googleCredential: GoogleCredential,
    private val primeDataStore: PrimeDataStore
) : SquadRepository {

    override fun observeConnectionState(): Flow<Boolean> = remoteDataSource.observeConnectionState()

    override suspend fun saveProfile(
        nickname: String,
        platform: Platform,
        region: Region
    ): Result<UserProfile> = runCatching {
        val userId = getRequiredUserId()
        val isConnected = remoteDataSource.observeConnectionState().first()
        if (!isConnected) throw IllegalStateException("Cannot update profile while offline.")

        val profile = UserProfile(
            userId = userId,
            nickname = nickname,
            platform = platform,
            region = region,
            updatedAt = System.currentTimeMillis()
        )
        val dto = SquadMapper.mapProfileToDto(profile)
        remoteDataSource.saveProfile(dto).getOrThrow()
        profile
    }

    override suspend fun getProfile(): Result<UserProfile?> = runCatching {
        val userId = getRequiredUserId()
        val dto = remoteDataSource.getProfile(userId).getOrThrow()
        dto?.let { SquadMapper.mapProfileToDomain(it) }
    }

    override suspend fun createAnnouncement(
        targetId: String,
        note: String?
    ): Result<SquadAnnouncement> = runCatching {
        val userId = getRequiredUserId()
        val isConnected = remoteDataSource.observeConnectionState().first()
        if (!isConnected) throw IllegalStateException("Cannot create announcement while offline.")

        val profile = getProfile().getOrThrow()
            ?: throw IllegalStateException("User profile must be created before publishing an announcement.")

        val offset = remoteDataSource.observeServerTimeOffset().first()
        val estimatedServerTime = System.currentTimeMillis() + offset

        val dto = AnnouncementDto(
            userId = userId,
            targetId = targetId,
            nickname = profile.nickname,
            platform = profile.platform.name,
            region = profile.region.name,
            createdAt = estimatedServerTime,
            expiresAt = estimatedServerTime + 300_000L,
            note = note
        )

        remoteDataSource.createAnnouncement(dto, estimatedServerTime).getOrThrow()

        val baseData = primeDataStore.baseData.first()
        val resolvedName = resolveTargetName(targetId, baseData)
        SquadMapper.mapAnnouncementToDomain(dto, userId, estimatedServerTime, resolvedName)
    }

    override suspend fun heartbeatAnnouncement(): Result<Unit> = runCatching {
        val userId = getRequiredUserId()
        val isConnected = remoteDataSource.observeConnectionState().first()
        if (!isConnected) throw IllegalStateException("Cannot send heartbeat while offline.")

        val offset = remoteDataSource.observeServerTimeOffset().first()
        val estimatedServerTime = System.currentTimeMillis() + offset

        remoteDataSource.heartbeatAnnouncement(userId, estimatedServerTime).getOrThrow()
    }

    override suspend fun deleteAnnouncement(): Result<Unit> = runCatching {
        val userId = getRequiredUserId()
        val isConnected = remoteDataSource.observeConnectionState().first()
        if (!isConnected) throw IllegalStateException("Cannot delete announcement while offline.")

        remoteDataSource.deleteAnnouncement(userId).getOrThrow()
    }

    override fun observeOwnAnnouncement(): Flow<SquadAnnouncement?> {
        val currentUserId = googleCredential.getSignedInUser()?.userId ?: return flowOf(null)

        return combine(
            remoteDataSource.observeAnnouncements(),
            remoteDataSource.observeServerTimeOffset(),
            primeDataStore.baseData
        ) { dtos, offset, baseData ->
            val estimatedServerTime = System.currentTimeMillis() + offset
            val ownDto = dtos.firstOrNull { it.userId == currentUserId } ?: return@combine null

            val isExpired = ownDto.expiresAt <= estimatedServerTime
            if (isExpired) return@combine null

            val targetName = resolveTargetName(ownDto.targetId, baseData)
            SquadMapper.mapAnnouncementToDomain(ownDto, currentUserId, estimatedServerTime, targetName)
        }
    }

    override fun observePublicAnnouncements(
        targetIdFilter: String?,
        platformFilter: Platform?,
        regionFilter: Region?
    ): Flow<List<SquadAnnouncement>> {
        val currentUserId = googleCredential.getSignedInUser()?.userId ?: ""

        return combine(
            remoteDataSource.observeAnnouncements(),
            remoteDataSource.observeServerTimeOffset(),
            primeDataStore.baseData
        ) { dtos, offset, baseData ->
            val estimatedServerTime = System.currentTimeMillis() + offset

            dtos.asSequence()
                // Filter out current user's own announcement from public list
                .filter { it.userId != currentUserId }
                // Filter out expired announcements
                .filter { it.expiresAt > estimatedServerTime }
                // Apply target filter if specified
                .filter { targetIdFilter.isNullOrEmpty() || it.targetId == targetIdFilter }
                // Apply platform filter if specified (Crossplay matches any)
                .filter { dto ->
                    platformFilter == null || platformFilter == Platform.CROSSPLAY || dto.platform == platformFilter.name || dto.platform == Platform.CROSSPLAY.name
                }
                // Map to domain model
                .map { dto ->
                    val resolvedName = resolveTargetName(dto.targetId, baseData)
                    SquadMapper.mapAnnouncementToDomain(dto, currentUserId, estimatedServerTime, resolvedName)
                }
                // Region Ranking: Preferred region comes first, then newest creation time
                .sortedWith(
                    compareByDescending<SquadAnnouncement> { announcement ->
                        regionFilter == null || announcement.region == regionFilter
                    }.thenByDescending { it.createdAt }
                )
                .toList()
        }
    }

    private fun getRequiredUserId(): String {
        return googleCredential.getSignedInUser()?.userId
            ?: throw IllegalStateException("User must be logged in to perform Squad Finder operations.")
    }

    private fun resolveTargetName(targetId: String, data: PrimeBaseData): String {
        val set = data.sets.firstOrNull { it.id == targetId }
        if (set != null) return set.name

        val part = data.parts.firstOrNull { it.id == targetId }
        if (part != null) {
            val setName = data.sets.firstOrNull { it.id == part.primeSetId }?.name ?: ""
            return StringFormatter.formatPartName(setName, part.part)
        }

        val relic = data.relics.firstOrNull { it.id == targetId }
        if (relic != null) return "${relic.era.name} ${relic.name}"

        return when (targetId) {
            GoalTargetType.FORMA.name -> "Forma"
            GoalTargetType.DUCAT.name -> "Ducats"
            else -> targetId
        }
    }
}
