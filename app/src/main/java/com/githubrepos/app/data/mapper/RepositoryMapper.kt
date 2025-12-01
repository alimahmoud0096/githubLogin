package com.githubrepos.app.data.mapper

import com.githubrepos.app.data.remote.dto.OwnerDto
import com.githubrepos.app.data.remote.dto.RepositoryDto
import com.githubrepos.app.domain.model.Owner
import com.githubrepos.app.domain.model.Repository

fun RepositoryDto.toDomain(): Repository {
    return Repository(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        isPrivate = isPrivate,
        starsCount = starsCount,
        language = language,
        updatedAt = updatedAt,
        owner = owner.toDomain()
    )
}

fun OwnerDto.toDomain(): Owner {
    return Owner(
        login = login,
        avatarUrl = avatarUrl
    )
}

