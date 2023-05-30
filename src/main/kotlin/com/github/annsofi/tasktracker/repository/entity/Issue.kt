package com.github.annsofi.tasktracker.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
data class Issue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val title: String,

    @Enumerated(EnumType.STRING)
    val type: IssueType,

    @Enumerated(EnumType.STRING)
    val state: IssueState,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val created: Instant? = null,

    @UpdateTimestamp
    @Column(nullable = false)
    val lastUpdated: Instant? = null,

    val userId: Long? = null,

    val parentId: Long? = null,
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
