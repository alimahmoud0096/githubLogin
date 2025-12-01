package com.githubrepos.app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BranchDto(
    val name: String,
    val commit: CommitDto,
    @Json(name = "protected")
    val isProtected: Boolean
)

@JsonClass(generateAdapter = true)
data class CommitDto(
    val sha: String
)

