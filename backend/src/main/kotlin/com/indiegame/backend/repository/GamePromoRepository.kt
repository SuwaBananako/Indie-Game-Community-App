package com.indiegame.backend.repository

import com.indiegame.backend.entity.GamePromo
import org.springframework.data.jpa.repository.JpaRepository

interface GamePromoRepository : JpaRepository<GamePromo, Long> {
    fun findByStatusOrderByCreateTimeDesc(status: String): List<GamePromo>
    fun findByAuthorUsernameOrderByCreateTimeDesc(authorUsername: String): List<GamePromo>
}
