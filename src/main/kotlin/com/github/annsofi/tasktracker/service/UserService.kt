package com.github.annsofi.tasktracker.service

import com.github.annsofi.tasktracker.model.UserDto
import com.github.annsofi.tasktracker.repository.UserRepository
import com.github.annsofi.tasktracker.repository.entity.User
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(private val userRepository: UserRepository) {
    fun addUser(userDto: UserDto): UserDto {
        val user = User(name = userDto.name, email = userDto.email)
        val savedUser = userRepository.save(user)
        return toDto(savedUser)
    }

    fun removeUser(userId: Long) {
        userRepository.deleteById(userId)
    }

    fun getUsers(): List<UserDto> {
        val users = userRepository.findAll()
        return users.map { toDto(it) }
    }

    fun getUser(userId: Long): UserDto {
        val user = userRepository.findById(userId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }
        return toDto(user)
    }

    private fun toDto(user: User): UserDto {
        return UserDto(
            id = user.id,
            name = user.name,
            email = user.email,
            // TODO
        )
    }
}
