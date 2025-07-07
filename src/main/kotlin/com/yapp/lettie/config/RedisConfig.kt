package com.yapp.lettie.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RedisConfig {
    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Boolean> {
        val template = RedisTemplate<String, Boolean>()
        template.connectionFactory = redisConnectionFactory
        return template
    }
}
