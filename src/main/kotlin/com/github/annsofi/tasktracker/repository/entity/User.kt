package com.github.annsofi.tasktracker.repository.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "tasktracker_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // TODO: kolla upp
    val id: Long? = null,
    val name: String,
    val email: String,
    var password: String = "",
)
