package com.github.annsofi.tasktracker.repository

import com.github.annsofi.tasktracker.repository.entity.Issue
import org.springframework.data.jpa.repository.JpaRepository

interface IssueRepository : JpaRepository<Issue, Long>

