package com.indiegame.backend.controller

import com.indiegame.backend.dto.ApiResponse
import com.indiegame.backend.dto.PageResponse
import com.indiegame.backend.entity.Comment
import com.indiegame.backend.entity.Post
import com.indiegame.backend.service.PostService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postService: PostService
) {

    @GetMapping("/approved")
    fun getApproved(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ApiResponse<PageResponse<Map<String, Any?>>> {
        val p: Page<Post> = postService.getApprovedPosts(page, size)
        return ApiResponse(data = PageResponse(
            content = p.content.map { it.toMap() },
            totalPages = p.totalPages,
            totalElements = p.totalElements,
            currentPage = p.number
        ))
    }

    @GetMapping("/hot")
    fun getHot(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ApiResponse<PageResponse<Map<String, Any?>>> {
        val p = postService.getHotPosts(page, size)
        return ApiResponse(data = PageResponse(
            content = p.content.map { it.toMap() },
            totalPages = p.totalPages,
            totalElements = p.totalElements,
            currentPage = p.number
        ))
    }

    @GetMapping("/search")
    fun search(
        @RequestParam keyword: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ApiResponse<PageResponse<Map<String, Any?>>> {
        val p = postService.searchApprovedPosts(keyword, page, size)
        return ApiResponse(data = PageResponse(
            content = p.content.map { it.toMap() },
            totalPages = p.totalPages,
            totalElements = p.totalElements,
            currentPage = p.number
        ))
    }

    @GetMapping("/category/{category}")
    fun getByCategory(
        @PathVariable category: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ApiResponse<PageResponse<Map<String, Any?>>> {
        val p = postService.getApprovedPostsByCategory(category, page, size)
        return ApiResponse(data = PageResponse(
            content = p.content.map { it.toMap() },
            totalPages = p.totalPages,
            totalElements = p.totalElements,
            currentPage = p.number
        ))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<Map<String, Any?>> {
        val post = postService.getPostById(id) ?: return ApiResponse(code = 404, message = "帖子不存在")
        return ApiResponse(data = post.toMap())
    }

    @GetMapping("/my")
    fun getMy(@RequestParam username: String): ApiResponse<List<Map<String, Any?>>> {
        return ApiResponse(data = postService.getMyPosts(username).map { it.toMap() })
    }

    @PostMapping
    fun create(@RequestBody post: Post): ApiResponse<Map<String, Any?>> {
        return ApiResponse(data = postService.createPost(post).toMap())
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody body: Map<String, String>
    ): ApiResponse<Map<String, Any?>> {
        val post = postService.updatePost(
            id, body["title"] ?: "", body["content"] ?: "", body["category"] ?: ""
        )
        return ApiResponse(data = post.toMap())
    }

    @PostMapping("/{id}/like")
    fun like(@PathVariable id: Long): ApiResponse<String> {
        postService.likePost(id)
        return ApiResponse(message = "ok")
    }

    @GetMapping("/{id}/comments")
    fun getComments(@PathVariable id: Long): ApiResponse<List<Map<String, Any?>>> {
        val comments = postService.getComments(id).map {
            mapOf(
                "id" to it.id, "postId" to it.postId, "username" to it.username,
                "content" to it.content, "imagePath" to it.imagePath,
                "createTime" to it.createTime
            )
        }
        return ApiResponse(data = comments)
    }

    @PostMapping("/{id}/comments")
    fun addComment(@PathVariable id: Long, @RequestBody comment: Comment): ApiResponse<Map<String, Any?>> {
        comment.postId = id
        val saved = postService.addComment(comment)
        return ApiResponse(data = mapOf(
            "id" to saved.id, "postId" to saved.postId, "username" to saved.username,
            "content" to saved.content, "imagePath" to saved.imagePath,
            "createTime" to saved.createTime
        ))
    }

    private fun Post.toMap() = mapOf<String, Any?>(
        "id" to id, "title" to title, "content" to content,
        "category" to category, "coverImage" to coverImage, "contentImages" to contentImages,
        "username" to username, "likeCount" to likeCount, "commentCount" to commentCount,
        "collectCount" to collectCount, "status" to status,
        "createTime" to createTime, "updateTime" to updateTime
    )
}
