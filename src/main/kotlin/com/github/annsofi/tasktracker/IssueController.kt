package com.github.annsofi.tasktracker

import com.github.annsofi.tasktracker.model.IssueDto
import com.github.annsofi.tasktracker.service.IssueService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/issues")
class IssueController(private val issueService: IssueService) {

    @PostMapping
    fun addIssue(@RequestBody issue: IssueDto): IssueDto {
        return issueService.addIssue(issue)
    }

    @GetMapping("/{issueId}")
    fun getIssue(@PathVariable issueId: Long): IssueDto {
        return issueService.getIssue(issueId)
    }

    @GetMapping
    fun getIssues(): List<IssueDto> {
        return issueService.getIssues()
    }

    @PutMapping("/{issueId}")
    fun updateIssue(@PathVariable issueId: Long, @RequestBody issue: IssueDto): IssueDto {
        return issueService.updateIssue(issueId, issue)
    }

    @DeleteMapping("/{issueId}")
    fun deleteIssue(@PathVariable issueId: Long) {
        issueService.deleteIssue(issueId)
    }

    @PostMapping("/{issueId}/assign/{userId}")
    fun assignUserToIssue(@PathVariable issueId: Long, @PathVariable userId: Long) {
        issueService.assignUserToIssue(issueId, userId)
    }

    @PutMapping("/{issueId}/unassign")
    fun unassignUserFromIssue(@PathVariable issueId: Long) {
        issueService.unassignUserFromIssue(issueId)
    }

    @PutMapping("/{issueId}/state/{state}")
    fun setIssueState(@PathVariable issueId: Long, @PathVariable state: String) {
        issueService.setIssueState(issueId, state)
    }

    @PutMapping("/{issueId}/parent/{parentId}")
    fun setParentIssue(@PathVariable issueId: Long, @PathVariable parentId: Long) {
        issueService.setParentIssue(issueId, parentId)
    }
}
