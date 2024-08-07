package com.felipimatheuz.primehunt.model

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val name: String?
)
