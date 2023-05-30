package com.github.annsofi.tasktracker.model

import java.time.LocalDateTime

data class IssueDto(
    val id: Long? = null,
    val title: String,
    val type: IssueTypeDto,
    val state: IssueStateDto,
    val created: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
    val userId: Long? = null,
    val parentId: Long? = null,
)

enum class IssueTypeDto {
    TASK,
    STORY,
    EPIC,
}

enum class IssueStateDto {
    TODO,
    IN_PROGRESS,
    DONE,
}
