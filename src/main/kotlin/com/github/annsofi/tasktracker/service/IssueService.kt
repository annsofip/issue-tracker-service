package com.github.annsofi.tasktracker.service

import com.github.annsofi.tasktracker.model.IssueDto
import com.github.annsofi.tasktracker.model.IssueStateDto
import com.github.annsofi.tasktracker.model.IssueTypeDto
import com.github.annsofi.tasktracker.repository.IssueRepository
import com.github.annsofi.tasktracker.repository.UserRepository
import com.github.annsofi.tasktracker.repository.entity.Issue
import com.github.annsofi.tasktracker.repository.entity.IssueState
import com.github.annsofi.tasktracker.repository.entity.IssueType
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class IssueService(
    private val issueRepository: IssueRepository,
    private val userRepository: UserRepository,
) {

    fun addIssue(issueDto: IssueDto): IssueDto {
        val issue = toEntity(issueDto)
        val savedIssue = issueRepository.save(issue)
        return toDto(savedIssue)
    }

    fun getIssue(issueId: Long): IssueDto {
        val issue = issueRepository.findById(issueId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found") }
        return toDto(issue)
    }

    fun getIssues(): List<IssueDto> {
        val issues = issueRepository.findAll()
        return issues.map { toDto(it) }
    }

    fun deleteIssue(id: Long) {
        issueRepository.deleteById(id)
    }

    fun assignUserToIssue(issueId: Long, userId: Long) {
        val issue = issueRepository.findById(issueId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found") }

        // Only checking if user actually exists
        val user = userRepository.findById(userId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }

        val updatedIssue = issue.copy(userId = user.id)

        issueRepository.save(updatedIssue)
    }

    fun unassignUserFromIssue(issueId: Long) {
        val issue = issueRepository.findById(issueId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found") }
        val updatedIssue = issue.copy(userId = null)
        issueRepository.save(updatedIssue)
    }

    fun setIssueState(issueId: Long, state: String) {
        val issue = issueRepository.findById(issueId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found") }

        val newState = IssueState.valueOf(state.uppercase()) // TODO
        val updatedIssue = issue.copy(state = newState)

        issueRepository.save(updatedIssue)
    }

    fun setParentIssue(issueId: Long, parentId: Long) {
        val issue = issueRepository.findById(issueId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found") }

        val parentIssue = issueRepository.findById(parentId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Parent issue not found") }

        val updatedIssue = issue.copy(parentId = parentIssue.id)

        issueRepository.save(updatedIssue)
    }

    fun updateIssue(issueId: Long, updatedIssueDto: IssueDto): IssueDto {
        val issue = issueRepository.findById(issueId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found") }

        val updatedIssue = issue.copy(
            title = updatedIssueDto.title,
            state = toStateEntity(updatedIssueDto.state),
            type = toTypeEntity(updatedIssueDto.type),
            // TODO!!
        )

        val savedIssue = issueRepository.save(updatedIssue)

        return toDto(savedIssue)
    }

    private fun toDto(issue: Issue): IssueDto {
        return IssueDto(
            id = issue.id,
            title = issue.title,
            type = toTypeDto(issue.type),
            state = toStateDto(issue.state),
            created = issue.created,
            lastUpdated = issue.lastUpdated,
            userId = issue.userId,
            parentId = issue.parentId,
        )
    }

    private fun toStateDto(state: IssueState): IssueStateDto {
        return when (state) {
            IssueState.TODO -> IssueStateDto.TODO
            IssueState.IN_PROGRESS -> IssueStateDto.IN_PROGRESS
            IssueState.DONE -> IssueStateDto.DONE
        }
    }

    private fun toTypeDto(type: IssueType): IssueTypeDto {
        return when (type) {
            IssueType.EPIC -> IssueTypeDto.EPIC
            IssueType.STORY -> IssueTypeDto.STORY
            IssueType.TASK -> IssueTypeDto.TASK
        }
    }

    private fun toEntity(issueDto: IssueDto): Issue {
        return Issue(
            id = issueDto.id,
            title = issueDto.title,
            type = toTypeEntity(issueDto.type),
            state = toStateEntity(issueDto.state),
            created = issueDto.created,
            lastUpdated = issueDto.lastUpdated,
            userId = issueDto.userId,
            parentId = issueDto.parentId,
        )
    }

    private fun toTypeEntity(typeDto: IssueTypeDto): IssueType {
        return when (typeDto) {
            IssueTypeDto.EPIC -> IssueType.EPIC
            IssueTypeDto.STORY -> IssueType.STORY
            IssueTypeDto.TASK -> IssueType.TASK
            else -> throw IllegalArgumentException("Invalid issue type: $typeDto") // TODO
        }
    }

    private fun toStateEntity(stateDto: IssueStateDto): IssueState {
        return when (stateDto) {
            IssueStateDto.TODO -> IssueState.TODO
            IssueStateDto.IN_PROGRESS -> IssueState.IN_PROGRESS
            IssueStateDto.DONE -> IssueState.DONE
            else -> throw IllegalArgumentException("Invalid issue state: $stateDto") // TODO
        }
    }
}
