package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsuleDto
import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsulePayload
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.writer.TimeCapsuleLikeWriter
import com.yapp.lettie.api.timecapsule.service.writer.TimeCapsuleWriter
import com.yapp.lettie.api.user.service.reader.UserReader
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleLike
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class TimeCapsuleService(
    private val userReader: UserReader,
    private val timeCapsuleWriter: TimeCapsuleWriter,
    private val timeCapsuleReader: TimeCapsuleReader,
    private val timeCapsuleLikeWriter: TimeCapsuleLikeWriter,
    private val timeCapsuleLikeReader: TimeCapsuleLikeReader,
) {
    @Transactional
    fun createTimeCapsule(
        userId: Long,
        payload: CreateTimeCapsulePayload,
    ): CreateTimeCapsuleDto {
        val user = userReader.getById(userId)

        if (payload.openAt.isBefore(LocalDateTime.now())) {
            throw ApiErrorException(ErrorMessages.INVALID_OPEN_AT)
        }

        if (payload.closedAt.isAfter(payload.openAt)) {
            throw ApiErrorException(ErrorMessages.INVALID_CLOSED_AT)
        }

        val capsule = TimeCapsule.of(user, generateInviteCode(), payload)
        val savedCapsule = timeCapsuleWriter.save(capsule)

        return CreateTimeCapsuleDto.of(
            id = savedCapsule.id,
            inviteCode = savedCapsule.inviteCode,
        )
    }

    @Transactional
    fun joinTimeCapsule(
        userId: Long,
        capsuleId: Long,
    ) {
        val capsule = timeCapsuleReader.getById(capsuleId)
        val user = userReader.getById(userId)

        if (capsule.isClosed(LocalDateTime.now())) {
            throw ApiErrorException(ErrorMessages.CLOSED_TIME_CAPSULE)
        }

        if (capsule.timeCapsuleUsers.any { it.user.id == user.id && it.isActive }) {
            throw ApiErrorException(ErrorMessages.ALREADY_JOINED)
        }

        val timeCapsuleUser = TimeCapsuleUser.of(user, capsule)
        capsule.addUser(timeCapsuleUser)
        user.addTimeCapsuleUser(timeCapsuleUser)
    }

    @Transactional
    fun like(
        userId: Long,
        capsuleId: Long,
    ) {
        val user = userReader.getById(userId)
        val capsule = timeCapsuleReader.getById(capsuleId)

        val existing = timeCapsuleLikeReader.findByUserIdAndCapsuleId(userId, capsuleId)
        if (existing == null) {
            val like = TimeCapsuleLike.of(user, capsule)
            timeCapsuleLikeWriter.save(like)
        } else if (!existing.isLiked) {
            existing.isLiked = true
            timeCapsuleLikeWriter.save(existing)
        }
    }

    @Transactional
    fun unlike(
        userId: Long,
        capsuleId: Long,
    ) {
        val existing = timeCapsuleLikeReader.findByUserIdAndCapsuleId(userId, capsuleId)
        if (existing != null && existing.isLiked) {
            existing.isLiked = false
            timeCapsuleLikeWriter.save(existing)
        }
    }

    @Transactional
    fun leaveTimeCapsule(
        userId: Long,
        capsuleId: Long,
    ) {
        val capsule = timeCapsuleReader.getById(capsuleId)

        val timeCapsuleUser =
            capsule.timeCapsuleUsers
                .firstOrNull { it.user.id == userId && it.isActive }
                ?: throw ApiErrorException(ErrorMessages.NOT_JOINED_CAPSULE)

        timeCapsuleUser.leave()
    }

    private fun generateInviteCode(): String = UUID.randomUUID().toString().take(RANDOM_VALUE_LENGTH)

    companion object {
        private const val RANDOM_VALUE_LENGTH = 8
    }
}
