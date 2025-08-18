package com.yapp.lettie.domain.timecapsule.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.lettie.domain.timecapsule.dto.RecipientRow
import com.yapp.lettie.domain.timecapsule.entity.QTimeCapsuleUser
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleUserStatus
import com.yapp.lettie.domain.user.entity.QUser

class TimeCapsuleUserCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : TimeCapsuleUserCustomRepository {
    override fun findRecipientsByCapsuleIds(capsuleIds: List<Long>): List<RecipientRow> {
        val timeCapsuleUser = QTimeCapsuleUser.timeCapsuleUser
        val user = QUser.user

        return queryFactory
            .select(
                Projections.constructor(
                    RecipientRow::class.java,
                    timeCapsuleUser.timeCapsule.id,
                    user.email,
                    user.nickname.coalesce(""),
                ),
            )
            .from(timeCapsuleUser)
            .join(timeCapsuleUser.user, user)
            .where(
                timeCapsuleUser.timeCapsule.id.`in`(capsuleIds)
                    .and(timeCapsuleUser.status.eq(TimeCapsuleUserStatus.ACTIVE)),
            )
            .fetch()
    }
}
