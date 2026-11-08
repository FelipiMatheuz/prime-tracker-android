package com.felipimatheuz.primehunt.data.remote.dto

import androidx.annotation.Keep

@Keep
data class ProfileDto(
    var userId: String = "",
    var nickname: String = "",
    var platform: String = "",
    var region: String = "",
    var updatedAt: Long = 0L
)

@Keep
data class AnnouncementDto(
    var userId: String = "",
    var targetId: String = "",
    var nickname: String = "",
    var platform: String = "",
    var region: String = "",
    var createdAt: Long = 0L,
    var expiresAt: Long = 0L,
    var note: String? = null
)
