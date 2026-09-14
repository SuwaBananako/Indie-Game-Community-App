package com.indiegame.backend.controller

import com.indiegame.backend.dto.ApiResponse
import com.indiegame.backend.service.NotificationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    @GetMapping
    fun getNotifications(@RequestParam username: String): ApiResponse<List<Map<String, Any?>>> {
        val list = notificationService.findByUsername(username).map {
            mapOf(
                "id" to it.id, "username" to it.username,
                "message" to it.message, "isRead" to it.isRead,
                "createTime" to it.createTime
            )
        }
        return ApiResponse(data = list)
    }

    @GetMapping("/unread-count")
    fun unreadCount(@RequestParam username: String): ApiResponse<Int> {
        return ApiResponse(data = notificationService.countUnread(username))
    }

    @PostMapping("/read-all")
    fun markAllRead(@RequestParam username: String): ApiResponse<String> {
        notificationService.markAllRead(username)
        return ApiResponse(message = "ok")
    }

    @PostMapping("/{id}/read")
    fun markRead(@PathVariable id: Long): ApiResponse<String> {
        notificationService.markRead(id)
        return ApiResponse(message = "ok")
    }
}
