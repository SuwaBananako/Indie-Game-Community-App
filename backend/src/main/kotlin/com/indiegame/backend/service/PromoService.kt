package com.indiegame.backend.service

import com.indiegame.backend.entity.*
import com.indiegame.backend.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PromoService(
    private val gamePromoRepository: GamePromoRepository,
    private val notificationRepository: NotificationRepository
) {

    fun getApprovedPromos() = gamePromoRepository.findByStatusOrderByCreateTimeDesc("approved")
    fun getPendingPromos() = gamePromoRepository.findByStatusOrderByCreateTimeDesc("pending")
    fun getPromosByAuthor(username: String) = gamePromoRepository.findByAuthorUsernameOrderByCreateTimeDesc(username)

    @Transactional
    fun createPromo(promo: GamePromo): GamePromo {
        promo.status = "pending"
        promo.createTime = System.currentTimeMillis()
        return gamePromoRepository.save(promo)
    }

    @Transactional
    fun approvePromo(id: Long): GamePromo {
        val promo = gamePromoRepository.findById(id).orElseThrow { RuntimeException("宣发不存在") }
        promo.status = "approved"
        notificationRepository.save(Notification(
            username = promo.authorUsername,
            message = "你的宣发项目《${promo.name}》已通过管理员审核！"
        ))
        return gamePromoRepository.save(promo)
    }

    @Transactional
    fun rejectPromo(id: Long): GamePromo {
        val promo = gamePromoRepository.findById(id).orElseThrow { RuntimeException("宣发不存在") }
        promo.status = "rejected"
        notificationRepository.save(Notification(
            username = promo.authorUsername,
            message = "你的宣发项目《${promo.name}》未通过管理员审核。"
        ))
        return gamePromoRepository.save(promo)
    }

    @Transactional
    fun dislikePromo(id: Long): GamePromo {
        val promo = gamePromoRepository.findById(id).orElseThrow { RuntimeException("宣发不存在") }
        promo.dislikeCount++
        return gamePromoRepository.save(promo)
    }

    fun getPromoById(id: Long) = gamePromoRepository.findById(id).orElse(null)

    @Transactional
    fun deletePromo(id: Long) {
        gamePromoRepository.deleteById(id)
    }
}
