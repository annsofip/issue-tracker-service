package com.github.annsofi.tasktracker.repository.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class Issue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    val title: String,

    @Enumerated(EnumType.STRING)
    val type: IssueType,

    @Enumerated(EnumType.STRING)
    val state: IssueState,

    val created: LocalDateTime,

    val lastUpdated: LocalDateTime,

    val userId: Long?,

    val parentId: Long?,
)

enum class IssueType {
    TASK,
    STORY,
    EPIC,
}

enum class IssueState {
    TODO,
    IN_PROGRESS,
    DONE,
}
