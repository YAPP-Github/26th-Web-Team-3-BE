package com.yapp.lettie.domain.timecapsule.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.OrderSpecifier
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
    @Suppress("ktlint:standard:no-consecutive-comments")
    override fun getTimeCapsulesByStatus(
        type: TimeCapsuleStatus?,
        now: LocalDateTime,
        pageable: Pageable,
    ): Page<TimeCapsule> {
        val builder = BooleanBuilder()
        builder.and(timeCapsule.accessType.eq(AccessType.PUBLIC))

        when (type) {
            TimeCapsuleStatus.OPENED -> {
                builder.and(timeCapsule.openAt.before(now))
            }

            TimeCapsuleStatus.WAITING_OPEN -> {
                builder.and(timeCapsule.openAt.after(now).and(timeCapsule.closedAt.before(now)))
            }

            TimeCapsuleStatus.WRITABLE -> {
                builder.and(timeCapsule.closedAt.after(now))
            }

            null -> {
            }
        }

        val query =
            queryFactory
                .select(timeCapsule)
                .from(timeCapsule)
                .leftJoin(letter)
                .on(letter.timeCapsule.id.eq(timeCapsule.id))
                .where(timeCapsule.accessType.eq(AccessType.PUBLIC).and(builder))
                .groupBy(timeCapsule.id)

        // 기본 정렬: 편지 수 내림차순, 생성일 내림차순
        val orderSpecifiers = orderByLetterCount()

        // todo: 동적 정렬 추가
/*
        for (sort in pageable.sort) {
            val orderSpecifier =
                when (sort.property) {
                    "createdAt" -> {
                        if (sort.isAscending) timeCapsule.createdAt.asc() else timeCapsule.createdAt.desc()
                    }
                    "closedAt" -> {
                        if (sort.isAscending) timeCapsule.closedAt.asc() else timeCapsule.closedAt.desc()
                    }
                    "openAt" -> {
                        if (sort.isAscending) timeCapsule.openAt.asc() else timeCapsule.openAt.desc()
                    }
                    else -> continue // 지원하지 않는 정렬 속성은 무시
                }
            orderSpecifiers.add(orderSpecifier)
        }
*/

        query
            .orderBy(*orderSpecifiers.toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize)

        val result = query.fetch()

        val countQuery =
            queryFactory
                .select(timeCapsule.countDistinct())
                .from(timeCapsule)
                .leftJoin(letter)
                .on(letter.timeCapsule.id.eq(timeCapsule.id))
                .where(timeCapsule.accessType.eq(AccessType.PUBLIC).and(builder))

        return PageableExecutionUtils.getPage(result, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }

    override fun findTimeCapsulesByTitle(
        title: String,
        accessType: AccessType,
        pageable: Pageable,
    ): Page<TimeCapsule> {
        val query =
            queryFactory
                .select(timeCapsule)
                .from(timeCapsule)
                .leftJoin(letter)
                .on(letter.timeCapsule.id.eq(timeCapsule.id))
                .where(
                    timeCapsule.title
                        .contains(title)
                        .and(timeCapsule.accessType.eq(accessType)),
                ).groupBy(timeCapsule.id)

        // 기본 정렬: 편지 수 내림차순, ID 내림차순
        val orderSpecifiers = orderByLetterCount()

        query
            .orderBy(*orderSpecifiers.toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize)

        val result = query.fetch()

        val countQuery =
            queryFactory
                .select(timeCapsule.countDistinct())
                .from(timeCapsule)
                .leftJoin(letter)
                .on(letter.timeCapsule.id.eq(timeCapsule.id))
                .where(
                    timeCapsule.title
                        .containsIgnoreCase(title)
                        .and(timeCapsule.accessType.eq(accessType)),
                )

        return PageableExecutionUtils.getPage(result, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }

    private fun orderByLetterCount(): MutableList<OrderSpecifier<*>> {
        val orderSpecifiers = mutableListOf<OrderSpecifier<*>>()
        orderSpecifiers.add(letter.count().desc())
        orderSpecifiers.add(timeCapsule.id.desc())
        return orderSpecifiers
    }
}
