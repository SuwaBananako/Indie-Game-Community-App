package com.indiegame.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "comments")
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "post_id", nullable = false)
    var postId: Long = 0,

    @Column(nullable = false, length = 50)
    var username: String = "",

    @Column(columnDefinition = "TEXT")
    var content: String = "",

    @Column(name = "image_path", columnDefinition = "TEXT")
    var imagePath: String = "",

    @Column(name = "create_time")
    var createTime: Long = Instant.now().toEpochMilli()
)
