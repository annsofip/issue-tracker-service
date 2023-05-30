package com.github.annsofi.tasktracker.service

import com.github.annsofi.tasktracker.model.UserDto
import com.github.annsofi.tasktracker.repository.UserRepository
import com.github.annsofi.tasktracker.repository.entity.User
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.server.ResponseStatusException

class UserServiceTest {

    val userRepository: UserRepository = mockk()

    @InjectMockKs
    var userService = UserService(userRepository)

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `addUser should save the user in the repository`() {
        // given
        val user = User(id = null, name = "Ann", email = "ann@example.com")
        every { userRepository.save(user) } returns user

        // when
        val result = userService.addUser(UserDto(id = null, name = "Ann", email = "ann@example.com"))

        // then
        verify(exactly = 1) { userRepository.save(user) }
        assertEquals(user.name, result.name)
        assertEquals(user.email, result.email)
    }

    @Test
    fun `removeUser should remove the user from the repository`() {
        // given
        val userId = 1L
        every { userRepository.deleteById(userId) } returns Unit

        // when
        userService.removeUser(userId)

        // then
        verify(exactly = 1) { userRepository.deleteById(userId) }
    }

    @Test
    fun `getUsers should return all users from the repository`() {
        // given
        val user1 = User(id = 1L, name = "Ann", email = "ann@example.com")
        val user2 = User(id = 2L, name = "Bob", email = "bob@example.com")
        every { userRepository.findAll() } returns listOf(user1, user2)

        // when
        val results = userService.getUsers()

        // then
        verify(exactly = 1) { userRepository.findAll() }
        assertEquals(2, results.size)
        assertEquals(user1.name, results[0].name)
        assertEquals(user2.name, results[1].name)
    }

    @Test
    fun `getUser should return the user from the repository`() {
        // given
        val userId = 1L
        val user = User(id = userId, name = "Ann", email = "ann@example.com")
        every { userRepository.findById(userId) } returns Optional.of(user)

        // when
        val result = userService.getUser(userId)

        // then
        verify(exactly = 1) { userRepository.findById(userId) }
        assertEquals(user.name, result.name)
        assertEquals(user.email, result.email)
    }

    @Test
    fun `getUser should return null if user doesn't exist`() {
        // given
        val nonExistentUserId = 100L
        every { userRepository.findById(nonExistentUserId) } returns Optional.empty()

        // when
        val exception = assertThrows<ResponseStatusException> {
            userService.getUser(nonExistentUserId)
        }

        // then
        assertEquals("404 NOT_FOUND \"User not found\"", exception.message)
    }

    @Test
    fun `addUser should throw exception if email already exists`() {
        // given
        val userDto = UserDto(id = null, name = "Ann", email = "ann@example.com")
        val user = User(id = null, name = "Ann", email = "ann@example.com")
        every { userRepository.save(user) } throws DataIntegrityViolationException("Duplicate email")

        // when
        val exception = assertThrows<DataIntegrityViolationException> {
            userService.addUser(userDto)
        }

        // then
        assertEquals("Duplicate email", exception.message)
    }
}
