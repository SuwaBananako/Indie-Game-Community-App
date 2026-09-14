package com.indiegame.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "votes")
data class Vote(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "game_name", nullable = false, length = 100)
    var gameName: String = "",

    @Column(columnDefinition = "TEXT")
    var description: String = "",

    @Column(name = "content_images", columnDefinition = "TEXT")
    var contentImages: String = "",

    @Column(name = "icon_path", columnDefinition = "TEXT")
    var iconPath: String = "",

    @Column(name = "banner_path", columnDefinition = "TEXT")
    var bannerPath: String = "",

    @Column(name = "author_username", nullable = false, length = 50)
    var authorUsername: String = "",

    @Column(length = 20)
    var status: String = "pending",

    @Column(name = "vote_count")
    var voteCount: Int = 0,

    @Column(name = "create_time")
    var createTime: Long = Instant.now().toEpochMilli()
)
