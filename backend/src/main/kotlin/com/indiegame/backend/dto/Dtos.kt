package com.indiegame.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val nickname: String? = null,
    @get:JsonProperty("isAdmin") val admin: Boolean = false
)

data class LoginResponse(
    val token: String,
    val username: String,
    val nickname: String,
    val avatar: String,
    @get:JsonProperty("isAdmin") val admin: Boolean
)

data class ApiResponse<T>(
    val code: Int = 200,
    val message: String = "success",
    val data: T? = null
)

data class PageResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val currentPage: Int
)
