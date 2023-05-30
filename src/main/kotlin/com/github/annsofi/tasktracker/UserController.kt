package com.github.annsofi.tasktracker

import com.github.annsofi.tasktracker.model.UserDto
import com.github.annsofi.tasktracker.service.UserService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun addUser(@RequestBody userDto: UserDto): UserDto {
        return userService.addUser(userDto)
    }

    @DeleteMapping("/{id}")
    fun removeUser(@PathVariable id: Long) {
        userService.removeUser(id)
    }

    @GetMapping
    fun getUsers(): List<UserDto> {
        return userService.getUsers()
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): UserDto? {
        return userService.getUser(id)
    }
}
