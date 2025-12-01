package com.githubrepos.app.domain.model

data class Repository(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val isPrivate: Boolean,
    val starsCount: Int,
    val language: String?,
    val updatedAt: String,
    val owner: Owner
)

data class Owner(
    val login: String,
    val avatarUrl: String
)

