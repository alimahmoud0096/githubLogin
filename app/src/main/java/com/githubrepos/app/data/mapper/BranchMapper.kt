package com.githubrepos.app.data.mapper

import com.githubrepos.app.data.remote.dto.BranchDto
import com.githubrepos.app.domain.model.Branch

fun BranchDto.toDomain(): Branch {
    return Branch(
        name = name,
        sha = commit.sha,
        isProtected = isProtected
    )
}

