package com.githubrepos.app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RepositoryDto(
    val id: Long,
    val name: String,
    @Json(name = "full_name")
    val fullName: String,
    val description: String?,
    @Json(name = "private")
    val isPrivate: Boolean,
    @Json(name = "stargazers_count")
    val starsCount: Int,
    val language: String?,
    @Json(name = "updated_at")
    val updatedAt: String,
    val owner: OwnerDto
)

@JsonClass(generateAdapter = true)
data class OwnerDto(
    val login: String,
    @Json(name = "avatar_url")
    val avatarUrl: String
)

