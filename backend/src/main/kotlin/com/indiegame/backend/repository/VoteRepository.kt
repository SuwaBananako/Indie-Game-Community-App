package com.indiegame.backend.repository

import com.indiegame.backend.entity.Vote
import org.springframework.data.jpa.repository.JpaRepository

interface VoteRepository : JpaRepository<Vote, Long> {
    fun findByStatusOrderByVoteCountDesc(status: String): List<Vote>
    fun findByStatusOrderByCreateTimeDesc(status: String): List<Vote>
    fun findByAuthorUsernameOrderByCreateTimeDesc(authorUsername: String): List<Vote>
}
