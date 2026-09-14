package com.indiegame.backend.repository

import com.indiegame.backend.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByUsernameAndIsReadFalseOrderByCreateTimeDesc(username: String): List<Notification>
    fun findByUsernameOrderByCreateTimeDesc(username: String): List<Notification>
    fun countByUsernameAndIsReadFalse(username: String): Int
}
