package com.indiegame.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false, length = 50)
    var username: String = "",

    @Column(nullable = false)
    var password: String = "",

    @Column(length = 50)
    var nickname: String = "",

    @Column(columnDefinition = "TEXT")
    var avatar: String = "",

    @Column(columnDefinition = "TEXT")
    var description: String = "",

    @Column(name = "is_admin")
    var isAdmin: Boolean = false,

    @Column(name = "is_banned")
    var isBanned: Boolean = false,

    @Column(name = "create_time")
    var createTime: Long = Instant.now().toEpochMilli()
)
