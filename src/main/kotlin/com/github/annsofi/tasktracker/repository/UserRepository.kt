package com.github.annsofi.tasktracker.repository

import com.github.annsofi.tasktracker.repository.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>
