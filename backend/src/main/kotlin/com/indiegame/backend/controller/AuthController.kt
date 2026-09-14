package com.indiegame.backend.controller

import com.indiegame.backend.dto.ApiResponse
import com.indiegame.backend.dto.LoginRequest
import com.indiegame.backend.dto.LoginResponse
import com.indiegame.backend.dto.RegisterRequest
import com.indiegame.backend.service.AuthService
import com.indiegame.backend.repository.UserRepository
import jakarta.annotation.PostConstruct
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val userRepository: UserRepository
) {

    @PostConstruct
    fun init() {
        authService.initAdmin()
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ApiResponse<String> {
        return try {
            authService.register(request)
            ApiResponse(message = "注册成功")
        } catch (e: Exception) {
            ApiResponse(code = 400, message = e.message ?: "注册失败")
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ApiResponse<LoginResponse> {
        val token = authService.login(request.username, request.password)
        if (token == null) {
            return ApiResponse(code = 401, message = "用户名或密码错误")
        }
        val user = userRepository.findByUsername(request.username).orElseThrow()
        return ApiResponse(data = LoginResponse(
            token = token,
            username = user.username,
            nickname = user.nickname,
            avatar = user.avatar,
            admin = user.isAdmin
        ))
    }
}
