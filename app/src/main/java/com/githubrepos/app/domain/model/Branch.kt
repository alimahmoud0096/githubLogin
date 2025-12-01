package com.githubrepos.app.domain.model

data class Branch(
    val name: String,
    val sha: String,
    val isProtected: Boolean
)

