package com.github.annsofi.tasktracker.model

import java.time.Instant

data class InputIssueDto(
    val title: String,
    val type: IssueTypeDto,
    val state: IssueStateDto,
    val userId: Long? = null,
    val parentId: Long? = null,
)

data class OutputIssueDto(
    val id: Long? = null,
    val title: String,
    val type: IssueTypeDto,
    val state: IssueStateDto,
    val created: Instant = Instant.now(),
    val lastUpdated: Instant = Instant.now(),
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
