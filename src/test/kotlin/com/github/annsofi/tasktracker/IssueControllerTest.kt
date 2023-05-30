package com.github.annsofi.tasktracker

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.annsofi.tasktracker.model.IssueDto
import com.github.annsofi.tasktracker.model.IssueStateDto
import com.github.annsofi.tasktracker.model.IssueTypeDto
import com.github.annsofi.tasktracker.model.UserDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IssueControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `test create and retrieve issue`() {
        val newIssue = IssueDto(
            id = null,
            title = "Test issue",
            type = IssueTypeDto.TASK,
            state = IssueStateDto.TODO,
            created = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
            userId = null,
            parentId = null,
        )

        // Create
        mockMvc.perform(
            post("/api/issues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newIssue)),
        )
            .andExpect(status().isOk)

        // Verify
        mockMvc.perform(get("/api/issues"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.[*].title").value("Test issue"))
            .andExpect(jsonPath("$.[*].id").value(1))
    }

    @Test
    fun `create user, create issue and assign user to issue`() {
        // Create user
        val userDto = UserDto(id = null, name = "Isaiah Nielsen", email = "brain.guy@example.com")
        val userResponse = mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)),
        ).andExpect(status().isOk)
            .andReturn()

        val createdUser = objectMapper.readValue(userResponse.response.contentAsString, UserDto::class.java)

        // Create issue
        val issueDto = IssueDto(
            title = "Test issue",
            type = IssueTypeDto.STORY,
            state = IssueStateDto.TODO,
        )
        val issueResponse = mockMvc.perform(
            post("/api/issues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(issueDto)),
        ).andExpect(status().isOk)
            .andReturn()

        val createdIssue = objectMapper.readValue(issueResponse.response.contentAsString, IssueDto::class.java)

        // Assign user to issue
        mockMvc.perform(
            post("/api/issues/${createdIssue.id}/assign/${createdUser.id}")
                .contentType(MediaType.APPLICATION_JSON),
        ).andExpect(status().isOk)

        // Verify
        mockMvc.perform(get("/api/issues/${createdIssue.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Test issue"))
            .andExpect(jsonPath("$.userId").value(1))
    }

    @Test
    fun `create user, add story issue, set issue state to in progress, remove user`() {
        val userDto = UserDto(name = "John Doe", email = "john@example.com")
        val issueDto = IssueDto(title = "Test Story", type = IssueTypeDto.STORY, state = IssueStateDto.TODO)

        // Create User
        val createUserResponse = mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)),
        ).andExpect(status().isOk)
            .andReturn()

        val createdUserId = objectMapper.readValue(createUserResponse.response.contentAsString, UserDto::class.java).id

        // Create Issue
        val createIssueResponse = mockMvc.perform(
            post("/api/issues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(issueDto)),
        ).andExpect(status().isOk)
            .andReturn()

        val createdIssueId =
            objectMapper.readValue(createIssueResponse.response.contentAsString, IssueDto::class.java).id

        // Update Issue state to IN_PROGRESS
        mockMvc.perform(
            put("/api/issues/$createdIssueId/state/IN_PROGRESS"),
        ).andExpect(status().isOk)

        // Unassign user from Issue
        mockMvc.perform(
            put("/api/issues/$createdIssueId/unassign"),
        ).andExpect(status().isOk)

        // Validate that user is unassigned
        mockMvc.perform(
            get("/api/issues/$createdIssueId"),
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").doesNotExist())
    }
}
