package com.felipimatheuz.primehunt.domain.model.squad

data class SquadAnnouncement(
    val id: String,
    val userId: String,
    val targetId: String,
    val targetName: String = "",
    val nickname: String,
    val platform: Platform,
    val region: Region,
    val createdAt: Long,
    val expiresAt: Long,
    val note: String? = null,
    val isMine: Boolean = false,
    val isExpired: Boolean = false
)
