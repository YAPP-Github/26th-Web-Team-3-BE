package com.yapp.lettie.api.timecapsule.service.reader

import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleLike
import com.yapp.lettie.domain.timecapsule.repository.TimeCapsuleLikeRepository
import com.yapp.lettie.domain.user.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimeCapsuleLikeReader(
    private val likeRepository: TimeCapsuleLikeRepository,
) {
    @Transactional(readOnly = true)
    fun getByUserAndCapsule(user: User, capsule: TimeCapsule): TimeCapsuleLike {
        return findByUserAndCapsule(user, capsule)
            ?: throw ApiErrorException(ErrorMessages.CAPSULE_LIKE_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findByUserAndCapsule(user: User, capsule: TimeCapsule): TimeCapsuleLike? {
        return likeRepository.findByUserAndTimeCapsule(user, capsule)
    }
}
