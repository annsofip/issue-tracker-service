package com.github.annsofi.tasktracker.service

import com.github.annsofi.tasktracker.model.IssueDto
import com.github.annsofi.tasktracker.model.IssueStateDto
import com.github.annsofi.tasktracker.model.IssueTypeDto
import com.github.annsofi.tasktracker.repository.IssueRepository
import com.github.annsofi.tasktracker.repository.UserRepository
import com.github.annsofi.tasktracker.repository.entity.Issue
import com.github.annsofi.tasktracker.repository.entity.IssueState
import com.github.annsofi.tasktracker.repository.entity.IssueType
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.Optional

class IssueServiceTest {

    val issueRepository: IssueRepository = mockk()

    val userRepository: UserRepository = mockk()

    @InjectMockKs
    var issueService = IssueService(issueRepository, userRepository)

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `addIssue should save the issue in the repository`() {
        // given
        val created = LocalDateTime.now()
        val issue = Issue(
            id = null,
            title = "lobortis",
            type = IssueType.TASK,
            state = IssueState.IN_PROGRESS,
            created = created,
            lastUpdated = created,
            userId = null,
            parentId = null,
        )
        every { issueRepository.save(issue) } returns issue

        // when
        val result = issueService.addIssue(
            IssueDto(
                id = null,
                title = "lobortis",
                type = IssueTypeDto.TASK,
                state = IssueStateDto.IN_PROGRESS,
                created = created,
                lastUpdated = created,
                userId = null,
                parentId = null,
            ),
        )

        // then
        verify(exactly = 1) { issueRepository.save(issue) }
        assertEquals(issue.title, result.title)
        assertEquals(issue.type.name, result.type.name)
        assertEquals(issue.state.name, result.state.name)
    }

    @Test
    fun `getIssue should return the issue from the repository`() {
        // given
        val issueId = 1L
        val created = LocalDateTime.now()
        val issue = Issue(
            id = issueId,
            title = "lobortis",
            type = IssueType.TASK,
            state = IssueState.IN_PROGRESS,
            created = created,
            lastUpdated = created,
            userId = null,
            parentId = null,
        )
        every { issueRepository.findById(issueId) } returns Optional.of(issue)

        // when
        val result = issueService.getIssue(issueId)

        // then
        verify(exactly = 1) { issueRepository.findById(issueId) }
        assertEquals(issue.title, result.title)
        assertEquals(issue.type.name, result.type.name)
        assertEquals(issue.state.name, result.state.name)
    }

    @Test
    fun `getIssues should return all issues from the repository`() {
        // given
        val created = LocalDateTime.now()
        val issue1 = Issue(
            id = 1L,
            title = "lobortis",
            type = IssueType.TASK,
            state = IssueState.IN_PROGRESS,
            created = created,
            lastUpdated = created,
            userId = null,
            parentId = null,
        )
        val issue2 = Issue(
            id = 2L,
            title = "amet",
            type = IssueType.STORY,
            state = IssueState.DONE,
            created = created,
            lastUpdated = created,
            userId = null,
            parentId = null,
        )
        every { issueRepository.findAll() } returns listOf(issue1, issue2)

        // when
        val results = issueService.getIssues()

        // then
        verify(exactly = 1) { issueRepository.findAll() }
        assertEquals(2, results.size)
        assertEquals(issue1.title, results[0].title)
        assertEquals(issue2.title, results[1].title)
    }

    @Test
    fun `deleteIssue should remove the issue from the repository`() {
        // given
        val issueId = 1L
        every { issueRepository.deleteById(issueId) } returns Unit

        // when
        issueService.deleteIssue(issueId)

        // then
        verify(exactly = 1) { issueRepository.deleteById(issueId) }
    }

    @Test
    fun `updateIssue should change and save the issue in the repository`() {
        // given
        val issueId = 1L
        val created = LocalDateTime.now()
        val originalIssue = Issue(
            id = issueId,
            title = "lobortis",
            type = IssueType.TASK,
            state = IssueState.IN_PROGRESS,
            created = created,
            lastUpdated = created,
            userId = null,
            parentId = null,
        )
        val updatedIssueDto = IssueDto(
            id = issueId,
            title = "amet",
            type = IssueTypeDto.STORY,
            state = IssueStateDto.DONE,
            created = created,
            lastUpdated = created,
            userId = null,
            parentId = null,
        )
        every { issueRepository.findById(issueId) } returns Optional.of(originalIssue)
        every { issueRepository.save(any()) } returns originalIssue

        // when
        issueService.updateIssue(issueId, updatedIssueDto)

        // then
        verify(exactly = 1) { issueRepository.save(any()) }
    }

    @Test
    fun `getIssue should return null if issue doesn't exist`() {
        // given
        val nonExistentIssueId = 100L
        every { issueRepository.findById(nonExistentIssueId) } returns Optional.empty()

        // when
        val exception = assertThrows<ResponseStatusException> {
            issueService.getIssue(nonExistentIssueId)
        }

        // then
        assertEquals("404 NOT_FOUND \"Issue not found\"", exception.message)
    }

    @Test
    fun `assignUserToIssue should throw exception if user doesn't exist`() {
        // given
        val issueId = 1L
        val nonExistentUserId = 100L
        every { issueRepository.findById(issueId) } returns Optional.of(
            Issue(
                id = null,
                title = "quam",
                type = IssueType.TASK,
                state = IssueState.TODO,
                created = LocalDateTime.now(),
                lastUpdated = LocalDateTime.now(),
                userId = null,
                parentId = null,
            ),
        )
        every { userRepository.findById(nonExistentUserId) } returns Optional.empty()

        // when
        val exception = assertThrows<ResponseStatusException> {
            issueService.assignUserToIssue(issueId, nonExistentUserId)
        }

        // then
        assertEquals("404 NOT_FOUND \"User not found\"", exception.message)
    }
}
