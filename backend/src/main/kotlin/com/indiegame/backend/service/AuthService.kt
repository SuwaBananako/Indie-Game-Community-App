package com.indiegame.backend.service

import com.indiegame.backend.dto.RegisterRequest
import com.indiegame.backend.entity.User
import com.indiegame.backend.repository.UserRepository
import com.indiegame.backend.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    fun register(request: RegisterRequest): User {
        require(!userRepository.existsByUsername(request.username)) { "用户名已存在" }
        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            nickname = request.nickname ?: request.username,
            isAdmin = request.admin
        )
        return userRepository.save(user)
    }

    fun login(username: String, rawPassword: String): String? {
        val user = userRepository.findByUsername(username).orElse(null) ?: return null
        if (user.isBanned) return null
        return if (passwordEncoder.matches(rawPassword, user.password)) {
            jwtTokenProvider.generateToken(username)
        } else null
    }

    fun initAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            val admin = User(
                username = "admin",
                password = passwordEncoder.encode("admin123"),
                nickname = "管理员",
                isAdmin = true
            )
            userRepository.save(admin)
        }
    }
}
