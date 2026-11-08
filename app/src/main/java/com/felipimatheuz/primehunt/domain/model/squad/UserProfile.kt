package com.felipimatheuz.primehunt.domain.model.squad

data class UserProfile(
    val userId: String,
    val nickname: String,
    val platform: Platform,
    val region: Region,
    val updatedAt: Long = System.currentTimeMillis()
)
