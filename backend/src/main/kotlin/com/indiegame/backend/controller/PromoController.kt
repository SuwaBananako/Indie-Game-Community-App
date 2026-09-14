package com.indiegame.backend.controller

import com.indiegame.backend.dto.ApiResponse
import com.indiegame.backend.entity.GamePromo
import com.indiegame.backend.service.PromoService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/promos")
class PromoController(
    private val promoService: PromoService
) {

    @GetMapping("/approved")
    fun getApproved(): ApiResponse<List<Map<String, Any?>>> {
        return ApiResponse(data = promoService.getApprovedPromos().map { it.toMap() })
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        val p = promoService.getPromoById(id) ?: return ApiResponse(code = 404, message = "宣发不存在")
        return ApiResponse(data = p.toMap())
    }

    @GetMapping("/my")
    fun getMy(@RequestParam username: String): ApiResponse<List<Map<String, Any?>>> {
        return ApiResponse(data = promoService.getPromosByAuthor(username).map { it.toMap() })
    }

    @PostMapping
    fun create(@RequestBody promo: GamePromo): ApiResponse<Map<String, Any?>> {
        return ApiResponse(data = promoService.createPromo(promo).toMap())
    }

    @PostMapping("/{id}/dislike")
    fun dislike(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        return ApiResponse(data = promoService.dislikePromo(id).toMap())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ApiResponse<String> {
        promoService.deletePromo(id)
        return ApiResponse(message = "删除成功")
    }

    private fun GamePromo.toMap() = mapOf<String, Any?>(
        "id" to id, "name" to name, "description" to description,
        "coverImage" to coverImage, "contentImages" to contentImages,
        "authorUsername" to authorUsername, "dislikeCount" to dislikeCount,
        "platform" to platform, "status" to status, "createTime" to createTime
    )
}
