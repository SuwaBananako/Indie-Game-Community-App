package com.indiegame.backend.service

import com.indiegame.backend.entity.*
import com.indiegame.backend.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VoteService(
    private val voteRepository: VoteRepository,
    private val notificationRepository: NotificationRepository
) {

    fun getApprovedVotes() = voteRepository.findByStatusOrderByVoteCountDesc("approved")
    fun getPendingVotes() = voteRepository.findByStatusOrderByCreateTimeDesc("pending")
    fun getVotesByAuthor(username: String) = voteRepository.findByAuthorUsernameOrderByCreateTimeDesc(username)
    fun getVoteById(id: Long) = voteRepository.findById(id).orElse(null)

    @Transactional
    fun createVote(vote: Vote): Vote {
        vote.status = "pending"
        vote.createTime = System.currentTimeMillis()
        return voteRepository.save(vote)
    }

    @Transactional
    fun doVote(id: Long) {
        val vote = voteRepository.findById(id).orElseThrow { RuntimeException("游戏不存在") }
        vote.voteCount++
        voteRepository.save(vote)
    }

    @Transactional
    fun approveVote(id: Long): Vote {
        val vote = voteRepository.findById(id).orElseThrow { RuntimeException("游戏不存在") }
        vote.status = "approved"
        notificationRepository.save(Notification(
            username = vote.authorUsername,
            message = "你的游戏《${vote.gameName}》已通过管理员审核，现在可以在投票榜单中展示！"
        ))
        return voteRepository.save(vote)
    }

    @Transactional
    fun rejectVote(id: Long): Vote {
        val vote = voteRepository.findById(id).orElseThrow { RuntimeException("游戏不存在") }
        vote.status = "rejected"
        notificationRepository.save(Notification(
            username = vote.authorUsername,
            message = "你的游戏《${vote.gameName}》未通过管理员审核。"
        ))
        return voteRepository.save(vote)
    }

    @Transactional
    fun deleteVote(id: Long) = voteRepository.deleteById(id)
}
