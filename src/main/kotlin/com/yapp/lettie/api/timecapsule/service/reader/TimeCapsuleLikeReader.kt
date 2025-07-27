package com.yapp.lettie.api.timecapsule.service.reader

import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleLike
import com.yapp.lettie.domain.timecapsule.repository.TimeCapsuleLikeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimeCapsuleLikeReader(
    private val likeRepository: TimeCapsuleLikeRepository,
) {
    @Transactional(readOnly = true)
    fun getByUserIdAndCapsuleId(
        userId: Long,
        capsuleId: Long,
    ): TimeCapsuleLike {
        return findByUserIdAndCapsuleId(userId, capsuleId)
            ?: throw ApiErrorException(ErrorMessages.CAPSULE_LIKE_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findByUserIdAndCapsuleId(
        userId: Long,
        capsuleId: Long,
    ): TimeCapsuleLike? {
        return likeRepository.findByUserIdAndTimeCapsuleId(userId, capsuleId)
    }

    @Transactional(readOnly = true)
    fun getLikeCount(capsuleId: Long): Int {
        return likeRepository.countByTimeCapsuleIdAndIsLikedTrue(capsuleId)
    }
}
