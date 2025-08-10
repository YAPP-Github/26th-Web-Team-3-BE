package com.yapp.lettie.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        val apiInfo =
            Info()
                .title("API Documentation")
                .description("API documentation with HTTP Only Cookie Authentication")
                .version("1.0")

        return OpenAPI()
            .info(apiInfo)
    }
}
