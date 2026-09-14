package com.indiegame.backend.service

import com.indiegame.backend.repository.NotificationRepository
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {
    fun findByUsername(username: String) = notificationRepository.findByUsernameOrderByCreateTimeDesc(username)
    fun countUnread(username: String): Int = notificationRepository.countByUsernameAndIsReadFalse(username)

    fun markAllRead(username: String) {
        val unread = notificationRepository.findByUsernameAndIsReadFalseOrderByCreateTimeDesc(username)
        unread.forEach { it.isRead = true }
        notificationRepository.saveAll(unread)
    }

    fun markRead(id: Long) {
        val n = notificationRepository.findById(id).orElse(null) ?: return
        n.isRead = true
        notificationRepository.save(n)
    }
}
