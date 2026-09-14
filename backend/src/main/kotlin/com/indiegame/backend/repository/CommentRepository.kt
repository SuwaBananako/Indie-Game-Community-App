package com.indiegame.backend.repository

import com.indiegame.backend.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByPostIdOrderByCreateTimeDesc(postId: Long): List<Comment>
    fun countByPostId(postId: Long): Int
}
