package com.indiegame.backend.repository

import com.indiegame.backend.entity.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long> {
    fun findByStatusOrderByCreateTimeDesc(status: String, pageable: Pageable): Page<Post>
    fun findByUsernameOrderByCreateTimeDesc(username: String, pageable: Pageable): Page<Post>
    fun findByUsernameOrderByCreateTimeDesc(username: String): List<Post>
    fun findByStatusAndTitleContainingOrderByCreateTimeDesc(status: String, title: String, pageable: Pageable): Page<Post>
    fun findByStatusAndCategoryOrderByCreateTimeDesc(status: String, category: String, pageable: Pageable): Page<Post>
    fun findByStatusOrderByLikeCountDesc(status: String, pageable: Pageable): Page<Post>
}
