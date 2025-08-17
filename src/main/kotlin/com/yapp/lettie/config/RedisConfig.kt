package com.yapp.lettie.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration

@Configuration
class RedisConfig(
    private val redisConnectionFactory: RedisConnectionFactory,
) {
    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Boolean> {
        val template = RedisTemplate<String, Boolean>()
        template.connectionFactory = redisConnectionFactory
        return template
    }

    @Bean
    fun cacheManager(): RedisCacheManager =
        RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(USER_TOTAL_COUNT_TTL),
            ).build()

    companion object {
        private val USER_TOTAL_COUNT_TTL = Duration.ofMinutes(30)
    }
}
