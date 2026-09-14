package com.indiegame.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "crowdfundings")
data class Crowdfunding(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    var name: String = "",

    @Column(columnDefinition = "TEXT")
    var description: String = "",

    @Column(name = "cover_image", columnDefinition = "TEXT")
    var coverImage: String = "",

    @Column(name = "content_images", columnDefinition = "TEXT")
    var contentImages: String = "",

    @Column(name = "author_username", nullable = false, length = 50)
    var authorUsername: String = "",

    @Column(name = "target_amount")
    var targetAmount: Double = 0.0,

    @Column(name = "current_amount")
    var currentAmount: Double = 0.0,

    @Column(name = "supporter_count")
    var supporterCount: Int = 0,

    @Column(length = 20)
    var status: String = "pending",

    @Column(name = "create_time")
    var createTime: Long = Instant.now().toEpochMilli()
)
