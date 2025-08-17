package com.yapp.lettie.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    servers = [
        Server(url = "https://api.lettie.me", description = "운영 서버"),
        Server(url = "https://api-dev.lettie.me", description = "개발 서버"),
        Server(url = "http://localhost:8080", description = "로컬 서버"),
    ],
)
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
