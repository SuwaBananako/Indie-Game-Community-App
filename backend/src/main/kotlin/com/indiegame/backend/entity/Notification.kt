package com.indiegame.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 50)
    var username: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    var message: String = "",

    @Column(name = "is_read")
    var isRead: Boolean = false,

    @Column(name = "create_time")
    var createTime: Long = Instant.now().toEpochMilli()
)
