package com.indiegame.backend.service

import com.indiegame.backend.entity.*
import com.indiegame.backend.repository.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val notificationRepository: NotificationRepository
) {

    fun getApprovedPosts(page: Int, size: Int) =
        postRepository.findByStatusOrderByCreateTimeDesc("approved", PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime")))

    fun getMyPosts(username: String) =
        postRepository.findByUsernameOrderByCreateTimeDesc(username)

    fun searchApprovedPosts(keyword: String, page: Int, size: Int) =
        postRepository.findByStatusAndTitleContainingOrderByCreateTimeDesc("approved", keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime")))

    fun getApprovedPostsByCategory(category: String, page: Int, size: Int) =
        postRepository.findByStatusAndCategoryOrderByCreateTimeDesc("approved", category, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime")))

    fun getHotPosts(page: Int, size: Int) =
        postRepository.findByStatusOrderByLikeCountDesc("approved", PageRequest.of(page, size))

    fun getPendingPosts() =
        postRepository.findByStatusOrderByCreateTimeDesc("pending", PageRequest.of(0, 100)).content

    @Transactional
    fun createPost(post: Post): Post {
        post.status = "pending"
        post.createTime = System.currentTimeMillis()
        return postRepository.save(post)
    }

    @Transactional
    fun updatePost(id: Long, title: String, content: String, category: String): Post {
        val post = postRepository.findById(id).orElseThrow { RuntimeException("帖子不存在") }
        post.title = title
        post.content = content
        post.category = category
        post.status = "pending"
        post.updateTime = System.currentTimeMillis()
        return postRepository.save(post)
    }

    @Transactional
    fun approvePost(id: Long): Post {
        val post = postRepository.findById(id).orElseThrow { RuntimeException("帖子不存在") }
        post.status = "approved"
        notificationRepository.save(Notification(
            username = post.username,
            message = "你的帖子《${post.title}》已通过管理员审核！"
        ))
        return postRepository.save(post)
    }

    @Transactional
    fun rejectPost(id: Long): Post {
        val post = postRepository.findById(id).orElseThrow { RuntimeException("帖子不存在") }
        post.status = "rejected"
        notificationRepository.save(Notification(
            username = post.username,
            message = "你的帖子《${post.title}》未通过管理员审核。"
        ))
        return postRepository.save(post)
    }

    @Transactional
    fun deletePost(id: Long) = postRepository.deleteById(id)

    @Transactional
    fun likePost(id: Long) {
        val post = postRepository.findById(id).orElseThrow { RuntimeException("帖子不存在") }
        post.likeCount++
        postRepository.save(post)
    }

    fun getComments(postId: Long) = commentRepository.findByPostIdOrderByCreateTimeDesc(postId)

    @Transactional
    fun addComment(comment: Comment): Comment {
        val post = postRepository.findById(comment.postId).orElseThrow { RuntimeException("帖子不存在") }
        post.commentCount = commentRepository.countByPostId(comment.postId) + 1
        postRepository.save(post)
        return commentRepository.save(comment)
    }

    fun getPostById(id: Long) = postRepository.findById(id).orElse(null)
}
