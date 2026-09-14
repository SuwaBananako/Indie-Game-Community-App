package com.indiegame.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "posts")
data class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    var title: String = "",

    @Column(columnDefinition = "TEXT")
    var content: String = "",

    @Column(length = 50)
    var category: String = "",

    @Column(name = "cover_image", columnDefinition = "TEXT")
    var coverImage: String = "",

    @Column(name = "content_images", columnDefinition = "TEXT")
    var contentImages: String = "",

    @Column(nullable = false, length = 50)
    var username: String = "",

    @Column(name = "like_count")
    var likeCount: Int = 0,

    @Column(name = "comment_count")
    var commentCount: Int = 0,

    @Column(name = "collect_count")
    var collectCount: Int = 0,

    @Column(length = 20)
    var status: String = "pending",

    @Column(name = "create_time")
    var createTime: Long = Instant.now().toEpochMilli(),

    @Column(name = "update_time")
    var updateTime: Long = Instant.now().toEpochMilli()
)
