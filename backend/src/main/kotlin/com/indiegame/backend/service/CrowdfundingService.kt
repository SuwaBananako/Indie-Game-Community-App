package com.indiegame.backend.service

import com.indiegame.backend.entity.*
import com.indiegame.backend.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CrowdfundingService(
    private val crowdfundingRepository: CrowdfundingRepository,
    private val notificationRepository: NotificationRepository
) {

    fun getApprovedCFs() = crowdfundingRepository.findByStatusOrderByCreateTimeDesc("approved")
    fun getPendingCFs() = crowdfundingRepository.findByStatusOrderByCreateTimeDesc("pending")
    fun getCFsByAuthor(username: String) = crowdfundingRepository.findByAuthorUsernameOrderByCreateTimeDesc(username)

    @Transactional
    fun createCF(cf: Crowdfunding): Crowdfunding {
        cf.status = "pending"
        cf.createTime = System.currentTimeMillis()
        return crowdfundingRepository.save(cf)
    }

    @Transactional
    fun supportCF(id: Long, amount: Double): Crowdfunding {
        val cf = crowdfundingRepository.findById(id).orElseThrow { RuntimeException("众筹项目不存在") }
        cf.currentAmount += amount
        cf.supporterCount++
        return crowdfundingRepository.save(cf)
    }

    @Transactional
    fun approveCF(id: Long): Crowdfunding {
        val cf = crowdfundingRepository.findById(id).orElseThrow { RuntimeException("众筹不存在") }
        cf.status = "approved"
        notificationRepository.save(Notification(
            username = cf.authorUsername,
            message = "你的众筹项目《${cf.name}》已通过管理员审核！"
        ))
        return crowdfundingRepository.save(cf)
    }

    @Transactional
    fun rejectCF(id: Long): Crowdfunding {
        val cf = crowdfundingRepository.findById(id).orElseThrow { RuntimeException("众筹不存在") }
        cf.status = "rejected"
        notificationRepository.save(Notification(
            username = cf.authorUsername,
            message = "你的众筹项目《${cf.name}》未通过管理员审核。"
        ))
        return crowdfundingRepository.save(cf)
    }

    fun getCFById(id: Long) = crowdfundingRepository.findById(id).orElse(null)

    @Transactional
    fun deleteCF(id: Long) {
        crowdfundingRepository.deleteById(id)
    }
}
