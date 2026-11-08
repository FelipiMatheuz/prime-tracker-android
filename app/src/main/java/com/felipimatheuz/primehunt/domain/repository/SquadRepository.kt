package com.felipimatheuz.primehunt.domain.repository

import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.domain.model.squad.SquadAnnouncement
import com.felipimatheuz.primehunt.domain.model.squad.UserProfile
import kotlinx.coroutines.flow.Flow

interface SquadRepository {
    fun observeConnectionState(): Flow<Boolean>
    suspend fun saveProfile(nickname: String, platform: Platform, region: Region): Result<UserProfile>
    suspend fun getProfile(): Result<UserProfile?>
    suspend fun createAnnouncement(targetId: String, note: String? = null): Result<SquadAnnouncement>
    suspend fun heartbeatAnnouncement(): Result<Unit>
    suspend fun deleteAnnouncement(): Result<Unit>
    fun observeOwnAnnouncement(): Flow<SquadAnnouncement?>
    fun observePublicAnnouncements(
        targetIdFilter: String? = null,
        platformFilter: Platform? = null,
        regionFilter: Region? = null
    ): Flow<List<SquadAnnouncement>>
}
