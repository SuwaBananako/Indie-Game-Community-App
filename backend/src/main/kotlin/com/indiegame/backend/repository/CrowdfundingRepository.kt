package com.indiegame.backend.repository

import com.indiegame.backend.entity.Crowdfunding
import org.springframework.data.jpa.repository.JpaRepository

interface CrowdfundingRepository : JpaRepository<Crowdfunding, Long> {
    fun findByStatusOrderByCreateTimeDesc(status: String): List<Crowdfunding>
    fun findByAuthorUsernameOrderByCreateTimeDesc(authorUsername: String): List<Crowdfunding>
}
