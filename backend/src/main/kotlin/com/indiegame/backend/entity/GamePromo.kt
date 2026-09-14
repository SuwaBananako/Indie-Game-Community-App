package com.indiegame.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "game_promos")
data class GamePromo(
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

    @Column(name = "dislike_count")
    var dislikeCount: Int = 0,

    @Column(length = 50)
    var platform: String = "",

    @Column(length = 20)
    var status: String = "pending",

    @Column(name = "create_time")
    var createTime: Long = Instant.now().toEpochMilli()
)
