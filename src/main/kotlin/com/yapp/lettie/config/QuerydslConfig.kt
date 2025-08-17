package com.yapp.lettie.config

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuerydslConfig(
    private val entityManager: EntityManager,
) {
    @Bean
    fun queryFactory(): JPAQueryFactory = JPAQueryFactory(entityManager)
}

fun <T> JPAQuery<T>.limit(pageSize: Int): JPAQuery<T> = this.limit(pageSize.toLong())
