package com.indiegame.backend.controller

import com.indiegame.backend.dto.ApiResponse
import com.indiegame.backend.entity.Crowdfunding
import com.indiegame.backend.service.CrowdfundingService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/crowdfundings")
class CrowdfundingController(
    private val cfService: CrowdfundingService
) {

    @GetMapping("/approved")
    fun getApproved(): ApiResponse<List<Map<String, Any?>>> {
        return ApiResponse(data = cfService.getApprovedCFs().map { it.toMap() })
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        val cf = cfService.getCFById(id) ?: return ApiResponse(code = 404, message = "项目不存在")
        return ApiResponse(data = cf.toMap())
    }

    @GetMapping("/my")
    fun getMy(@RequestParam username: String): ApiResponse<List<Map<String, Any?>>> {
        return ApiResponse(data = cfService.getCFsByAuthor(username).map { it.toMap() })
    }

    @PostMapping
    fun create(@RequestBody cf: Crowdfunding): ApiResponse<Map<String, Any?>> {
        return ApiResponse(data = cfService.createCF(cf).toMap())
    }

    @PostMapping("/{id}/support")
    fun support(@PathVariable id: Long, @RequestBody body: Map<String, Double>): ApiResponse<Map<String, Any?>> {
        val amount = body["amount"] ?: 0.0
        return ApiResponse(data = cfService.supportCF(id, amount).toMap())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ApiResponse<String> {
        cfService.deleteCF(id)
        return ApiResponse(message = "删除成功")
    }

    private fun Crowdfunding.toMap() = mapOf<String, Any?>(
        "id" to id, "name" to name, "description" to description,
        "coverImage" to coverImage, "contentImages" to contentImages,
        "authorUsername" to authorUsername, "targetAmount" to targetAmount,
        "currentAmount" to currentAmount, "supporterCount" to supporterCount,
        "status" to status, "createTime" to createTime
    )
}
