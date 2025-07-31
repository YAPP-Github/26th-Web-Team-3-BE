package com.yapp.lettie.domain.timecapsule.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.lettie.config.limit
import com.yapp.lettie.domain.letter.entity.QLetter.letter
import com.yapp.lettie.domain.timecapsule.entity.QTimeCapsule.timeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import java.time.LocalDateTime

class TimeCapsuleCustomerRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : TimeCapsuleCustomerRepository {
    override fun getTimeCapsulesByStatus(
        type: TimeCapsuleStatus?,
        now: LocalDateTime,
        pageable: Pageable,
    ): Page<TimeCapsule> {
        val builder = BooleanBuilder()
        builder.and(timeCapsule.accessType.eq(AccessType.PUBLIC))

        when (type) {
            TimeCapsuleStatus.OPENED -> {
                builder.and(timeCapsule.openAt.after(now))
            }

            TimeCapsuleStatus.WAITING_OPEN -> {
                builder.and(timeCapsule.openAt.before(now).and(timeCapsule.closedAt.after(now)))
            }

            TimeCapsuleStatus.WRITABLE -> {
                builder.and(timeCapsule.closedAt.before(now))
            }

            null -> {
            }
        }

        val result =
            queryFactory
                .select(timeCapsule)
                .from(timeCapsule)
                .leftJoin(letter)
                .on(letter.timeCapsule.id.eq(timeCapsule.id))
                .where(timeCapsule.accessType.eq(AccessType.PUBLIC).and(builder))
                .groupBy(timeCapsule.id)
                .orderBy(letter.count().desc(), timeCapsule.createdAt.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize)
                .fetch()

        val countQuery =
            queryFactory
                .select(timeCapsule.countDistinct())
                .from(timeCapsule)
                .leftJoin(letter)
                .on(letter.timeCapsule.id.eq(timeCapsule.id))
                .where(timeCapsule.accessType.eq(AccessType.PUBLIC))

        return PageableExecutionUtils.getPage(result, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }
}
