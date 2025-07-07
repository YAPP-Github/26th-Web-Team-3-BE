package com.yapp.lettie.infrastructure.llm

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.WebClient

inline fun <reified T> WebClient.call(
    method: HttpMethod,
    uri: String,
    requestBody: Any,
    noinline headersConsumer: (HttpHeaders) -> Unit = {},
): T {
    return this.method(method)
        .uri(uri)
        .headers(headersConsumer)
        .bodyValue(requestBody)
        .retrieve()
        .onStatus({ it.isError }) { response ->
            response.bodyToMono(String::class.java)
                .map { RuntimeException("LLM API 호출 실패: $it") }
        }
        .bodyToMono(T::class.java)
        .block() ?: throw RuntimeException("LLM API 응답이 null입니다")
}
