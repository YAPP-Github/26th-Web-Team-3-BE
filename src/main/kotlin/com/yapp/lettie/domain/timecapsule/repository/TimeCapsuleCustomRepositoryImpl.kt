package com.yapp.lettie.domain.timecapsule.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.lettie.config.limit
import com.yapp.lettie.domain.letter.entity.QLetter.letter
import com.yapp.lettie.domain.timecapsule.entity.QTimeCapsule.timeCapsule
import com.yapp.lettie.domain.timecapsule.entity.QTimeCapsuleLike
import com.yapp.lettie.domain.timecapsule.entity.QTimeCapsuleUser
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import com.yapp.lettie.domain.timecapsule.entity.vo.CapsuleSort
import com.yapp.lettie.domain.timecapsule.entity.vo.MyCapsuleFilter
import com.yapp.lettie.domain.timecapsule.entity.vo.SortContext
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleUserStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import java.time.LocalDateTime

class TimeCapsuleCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : TimeCapsuleCustomRepository {
    @Suppress("ktlint:standard:no-consecutive-comments")
    override fun getTimeCapsulesByStatus(
        type: TimeCapsuleStatus?,
        sort: CapsuleSort,
        now: LocalDateTime,
        pageable: Pageable,
    ): Page<TimeCapsule> {
        val builder = BooleanBuilder().and(timeCapsule.accessType.eq(AccessType.PUBLIC))

        when (type) {
            TimeCapsuleStatus.OPENED -> builder.and(timeCapsule.openAt.before(now))
            TimeCapsuleStatus.WAITING_OPEN ->
                builder.and(
                    timeCapsule.openAt.after(now)
                        .and(timeCapsule.closedAt.before(now)),
                )

            TimeCapsuleStatus.WRITABLE -> builder.and(timeCapsule.closedAt.after(now))
            null -> {}
        }

        val orderSpecifiers = buildSortOrder(sort, now, SortContext.EXPLORE)

        val results =
            queryFactory
                .selectFrom(timeCapsule)
                .leftJoin(letter).on(letter.timeCapsule.id.eq(timeCapsule.id))
                .where(builder)
                .groupBy(timeCapsule.id)
                .orderBy(*orderSpecifiers.toTypedArray())
                .offset(pageable.offset)
                .limit(pageable.pageSize)
                .fetch()

        val countQuery =
            queryFactory
                .select(timeCapsule.countDistinct())
                .from(timeCapsule)
                .where(builder)

        return PageableExecutionUtils.getPage(results, pageable) {
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
                        .containsIgnoreCase(title)
                        .and(timeCapsule.accessType.eq(accessType)),
                ).groupBy(timeCapsule.id)

        // 기본 정렬: 편지 수 내림차순, 생성일 내림차순
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

    override fun getMyTimeCapsules(
        userId: Long,
        filter: MyCapsuleFilter,
        sort: CapsuleSort,
        now: LocalDateTime,
        pageable: Pageable,
    ): Page<TimeCapsule> {
        val like = QTimeCapsuleLike.timeCapsuleLike
        val participant = QTimeCapsuleUser.timeCapsuleUser
        val tcu = QTimeCapsuleUser("tcu")

        val builder =
            BooleanBuilder().apply {
                when (filter) {
                    MyCapsuleFilter.CREATED ->
                        and(timeCapsule.creator.id.eq(userId))

                    MyCapsuleFilter.LIKED ->
                        and(like.user.id.eq(userId).and(like.isLiked.isTrue))

                    MyCapsuleFilter.PARTICIPATING ->
                        and(participant.user.id.eq(userId).and(participant.status.eq(TimeCapsuleUserStatus.ACTIVE)))

                    MyCapsuleFilter.ALL ->
                        and(
                            timeCapsule.creator.id.eq(userId)
                                .or(like.user.id.eq(userId).and(like.isLiked.isTrue))
                                .or(
                                    participant.user.id.eq(
                                        userId,
                                    ).and(participant.status.eq(TimeCapsuleUserStatus.ACTIVE)),
                                ),
                        )
                }
            }

        val query =
            queryFactory
                .select(timeCapsule)
                .from(timeCapsule)
                .leftJoin(like).on(like.timeCapsule.id.eq(timeCapsule.id))
                .leftJoin(participant).on(participant.timeCapsule.id.eq(timeCapsule.id))
                .leftJoin(tcu).on(
                    tcu.timeCapsule.id.eq(timeCapsule.id)
                        .and(tcu.user.id.eq(userId)),
                )
                .where(builder)
                .groupBy(timeCapsule.id)

        val orderSpecifiers = buildSortOrder(sort, now, SortContext.MY, tcu)
        query.orderBy(*orderSpecifiers.toTypedArray())

        query.offset(pageable.offset).limit(pageable.pageSize)
        val result = query.fetch()

        val countQuery =
            queryFactory
                .select(timeCapsule.countDistinct())
                .from(timeCapsule)
                .leftJoin(like)
                .on(like.timeCapsule.id.eq(timeCapsule.id))
                .leftJoin(participant)
                .on(participant.timeCapsule.id.eq(timeCapsule.id))
                .leftJoin(tcu)
                .on(
                    tcu.timeCapsule.id.eq(timeCapsule.id)
                        .and(tcu.user.id.eq(userId)),
                )
                .where(builder)

        return PageableExecutionUtils.getPage(result, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }

    private fun buildSortOrder(
        sort: CapsuleSort,
        now: LocalDateTime,
        context: SortContext,
        tcu: QTimeCapsuleUser? = null,
    ): List<OrderSpecifier<*>> {
        return when (sort) {
            // 최신 생성 순
            CapsuleSort.LATEST -> listOf(timeCapsule.id.desc())
            /* 오픈 임박 순
            1순위: 아직 안열렸고, 오픈이 임박한 순
            2순위: 열린 캡슐, 최근에 열린순
             */
            CapsuleSort.OPEN_IMMINENT -> {
                val groupExpr =
                    CaseBuilder()
                        .`when`(timeCapsule.openAt.after(now)).then(0)
                        .otherwise(1)

                val futureDiff: NumberExpression<Long> =
                    Expressions.numberTemplate(
                        Long::class.java,
                        "CASE WHEN {0} > {1} THEN timestampdiff(SECOND, {1}, {0}) END",
                        timeCapsule.openAt,
                        now,
                    )

                listOf(
                    groupExpr.asc(),
                    futureDiff.asc(),
                    timeCapsule.openAt.desc(),
                )
            }
            /*  작성 마감순
           group 0 : 아직 작성 가능  (now < closedAt)
           group 1 : 작성 마감 & 오픈 전 (closedAt ≤ now < openAt)
           group 2 : 이미 열림      (now ≥ openAt)
             */
            CapsuleSort.WRITE_DEADLINE -> {
                val groupExpr =
                    CaseBuilder()
                        .`when`(timeCapsule.closedAt.after(now)).then(0)
                        .`when`(
                            timeCapsule.closedAt.before(now)
                                .and(timeCapsule.openAt.after(now)),
                        ).then(1)
                        .otherwise(2)

                listOf(
                    groupExpr.asc(),
                    timeCapsule.closedAt.asc(),
                    timeCapsule.openAt.asc(),
                )
            }

            CapsuleSort.DEFAULT ->
                when (context) {
                    // “내 캡슐” 기본 : 미열람 우선
                    SortContext.MY -> {
                        val priorityExpr =
                            CaseBuilder()
                                .`when`(
                                    timeCapsule.openAt.before(now)
                                        .and(tcu!!.isOpened.min().eq(false)),
                                ).then(0)
                                .otherwise(1)

                        listOf(
                            priorityExpr.asc(),
                            timeCapsule.updatedAt.desc(),
                            timeCapsule.id.desc(),
                        )
                    }

                    // “Explore” 기본 : 편지 수 ↓, ID ↓
                    SortContext.EXPLORE ->
                        listOf(
                            letter.count().desc(),
                            timeCapsule.id.desc(),
                        )
                }
        }
    }

    private fun orderByLetterCount(): MutableList<OrderSpecifier<*>> {
        val orderSpecifiers = mutableListOf<OrderSpecifier<*>>()
        orderSpecifiers.add(letter.count().desc())
        orderSpecifiers.add(timeCapsule.id.desc())
        return orderSpecifiers
    }
}
