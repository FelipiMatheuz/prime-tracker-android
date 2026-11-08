package com.felipimatheuz.primehunt.data.mapper

import com.felipimatheuz.primehunt.data.remote.dto.AnnouncementDto
import com.felipimatheuz.primehunt.data.remote.dto.ProfileDto
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.domain.model.squad.SquadAnnouncement
import com.felipimatheuz.primehunt.domain.model.squad.UserProfile

object SquadMapper {

    fun mapProfileToDomain(dto: ProfileDto): UserProfile {
        return UserProfile(
            userId = dto.userId,
            nickname = dto.nickname,
            platform = runCatching { Platform.valueOf(dto.platform) }.getOrDefault(Platform.CROSSPLAY),
            region = runCatching { Region.valueOf(dto.region) }.getOrDefault(Region.NORTH_AMERICA),
            updatedAt = dto.updatedAt
        )
    }

    fun mapProfileToDto(profile: UserProfile): ProfileDto {
        return ProfileDto(
            userId = profile.userId,
            nickname = profile.nickname,
            platform = profile.platform.name,
            region = profile.region.name,
            updatedAt = profile.updatedAt
        )
    }

    fun mapAnnouncementToDomain(
        dto: AnnouncementDto,
        currentUserId: String,
        currentServerTime: Long,
        resolvedTargetName: String
    ): SquadAnnouncement {
        val isExpired = dto.expiresAt <= currentServerTime
        val isMine = dto.userId.isNotEmpty() && dto.userId == currentUserId

        return SquadAnnouncement(
            id = dto.userId,
            userId = dto.userId,
            targetId = dto.targetId,
            targetName = resolvedTargetName,
            nickname = dto.nickname,
            platform = runCatching { Platform.valueOf(dto.platform) }.getOrDefault(Platform.CROSSPLAY),
            region = runCatching { Region.valueOf(dto.region) }.getOrDefault(Region.NORTH_AMERICA),
            createdAt = dto.createdAt,
            expiresAt = dto.expiresAt,
            note = dto.note,
            isMine = isMine,
            isExpired = isExpired
        )
    }

    fun mapAnnouncementToDto(announcement: SquadAnnouncement): AnnouncementDto {
        return AnnouncementDto(
            userId = announcement.userId,
            targetId = announcement.targetId,
            nickname = announcement.nickname,
            platform = announcement.platform.name,
            region = announcement.region.name,
            createdAt = announcement.createdAt,
            expiresAt = announcement.expiresAt,
            note = announcement.note
        )
    }
}
