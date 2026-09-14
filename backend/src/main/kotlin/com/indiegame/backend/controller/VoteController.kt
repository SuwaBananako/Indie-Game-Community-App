package com.indiegame.backend.controller

import com.indiegame.backend.dto.ApiResponse
import com.indiegame.backend.entity.Vote
import com.indiegame.backend.service.VoteService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/votes")
class VoteController(
    private val voteService: VoteService
) {

    @GetMapping("/approved")
    fun getApproved(): ApiResponse<List<Map<String, Any?>>> {
        val list = voteService.getApprovedVotes().map { it.toMap() }
        return ApiResponse(data = list)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        val vote = voteService.getVoteById(id)
            ?: return ApiResponse(code = 404, message = "投票不存在")
        return ApiResponse(data = vote.toMap())
    }

    @GetMapping("/my")
    fun getMy(@RequestParam username: String): ApiResponse<List<Map<String, Any?>>> {
        val list = voteService.getVotesByAuthor(username).map { it.toMap() }
        return ApiResponse(data = list)
    }

    @PostMapping
    fun create(@RequestBody vote: Vote): ApiResponse<Map<String, Any?>> {
        return ApiResponse(data = voteService.createVote(vote).toMap())
    }

    @PostMapping("/{id}/vote")
    fun vote(@PathVariable id: Long): ApiResponse<String> {
        voteService.doVote(id)
        return ApiResponse(message = "投票成功")
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ApiResponse<String> {
        voteService.deleteVote(id)
        return ApiResponse(message = "删除成功")
    }

    private fun Vote.toMap() = mapOf<String, Any?>(
        "id" to id, "gameName" to gameName, "description" to description,
        "contentImages" to contentImages, "iconPath" to iconPath,
        "bannerPath" to bannerPath, "authorUsername" to authorUsername,
        "status" to status, "voteCount" to voteCount, "createTime" to createTime
    )
}
