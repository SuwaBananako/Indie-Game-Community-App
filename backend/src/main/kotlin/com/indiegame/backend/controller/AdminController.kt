package com.indiegame.backend.controller

import com.indiegame.backend.dto.ApiResponse
import com.indiegame.backend.service.CrowdfundingService
import com.indiegame.backend.service.PostService
import com.indiegame.backend.service.PromoService
import com.indiegame.backend.service.VoteService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.UUID

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val postService: PostService,
    private val promoService: PromoService,
    private val voteService: VoteService,
    private val cfService: CrowdfundingService
) {

    // Posts
    @GetMapping("/posts/pending")
    fun pendingPosts(): ApiResponse<List<Map<String, Any?>>> {
        return ApiResponse(data = postService.getPendingPosts().map {
            mapOf("id" to it.id, "title" to it.title, "content" to it.content, "username" to it.username,
                "status" to it.status, "createTime" to it.createTime)
        })
    }

    @PostMapping("/posts/{id}/approve")
    fun approvePost(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        val p = postService.approvePost(id)
        return ApiResponse(data = mapOf("id" to p.id, "status" to p.status))
    }

    @PostMapping("/posts/{id}/reject")
    fun rejectPost(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        val p = postService.rejectPost(id)
        return ApiResponse(data = mapOf("id" to p.id, "status" to p.status))
    }

    @DeleteMapping("/posts/{id}")
    fun deletePost(@PathVariable id: Long): ApiResponse<String> {
        postService.deletePost(id)
        return ApiResponse(message = "删除成功")
    }

    // Promos
    @GetMapping("/promos/pending")
    fun pendingPromos(): ApiResponse<List<Map<String, Any?>>> {
        return ApiResponse(data = promoService.getPendingPromos().map {
            mapOf("id" to it.id, "name" to it.name, "description" to it.description,
                "authorUsername" to it.authorUsername, "status" to it.status, "createTime" to it.createTime)
        })
    }

    @PostMapping("/promos/{id}/approve")
    fun approvePromo(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        val p = promoService.approvePromo(id)
        return ApiResponse(data = mapOf("id" to p.id, "status" to p.status))
    }

    @PostMapping("/promos/{id}/reject")
    fun rejectPromo(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        val p = promoService.rejectPromo(id)
        return ApiResponse(data = mapOf("id" to p.id, "status" to p.status))
    }

    // Votes
    @GetMapping("/votes/pending")
    fun pendingVotes(): ApiResponse<List<Map<String, Any?>>> {
        return ApiResponse(data = voteService.getPendingVotes().map {
            mapOf("id" to it.id, "gameName" to it.gameName, "description" to it.description,
                "authorUsername" to it.authorUsername, "status" to it.status, "createTime" to it.createTime)
        })
    }

    @PostMapping("/votes/{id}/approve")
    fun approveVote(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        val v = voteService.approveVote(id)
        return ApiResponse(data = mapOf("id" to v.id, "status" to v.status))
    }

    @PostMapping("/votes/{id}/reject")
    fun rejectVote(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        val v = voteService.rejectVote(id)
        return ApiResponse(data = mapOf("id" to v.id, "status" to v.status))
    }

    // Crowdfundings
    @GetMapping("/crowdfundings/pending")
    fun pendingCFs(): ApiResponse<List<Map<String, Any?>>> {
        return ApiResponse(data = cfService.getPendingCFs().map {
            mapOf("id" to it.id, "name" to it.name, "description" to it.description,
                "authorUsername" to it.authorUsername, "status" to it.status, "createTime" to it.createTime)
        })
    }

    @PostMapping("/crowdfundings/{id}/approve")
    fun approveCF(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        val c = cfService.approveCF(id)
        return ApiResponse(data = mapOf("id" to c.id, "status" to c.status))
    }

    @PostMapping("/crowdfundings/{id}/reject")
    fun rejectCF(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        val c = cfService.rejectCF(id)
        return ApiResponse(data = mapOf("id" to c.id, "status" to c.status))
    }
}

@RestController
@RequestMapping("/api/upload")
class UploadController {

    @PostMapping("/image")
    fun uploadImage(@RequestParam file: MultipartFile): ApiResponse<String> {
        val ext = file.originalFilename?.substringAfterLast('.', "jpg") ?: "jpg"
        val filename = UUID.randomUUID().toString() + "." + ext
        val uploadDir = File("uploads")
        if (!uploadDir.exists()) uploadDir.mkdirs()
        file.transferTo(File(uploadDir, filename))
        return ApiResponse(data = "/uploads/$filename")
    }
}
