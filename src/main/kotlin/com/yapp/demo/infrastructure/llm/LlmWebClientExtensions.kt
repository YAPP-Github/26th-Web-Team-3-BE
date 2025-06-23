package com.yapp.demo.infrastructure.llm

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
        .bodyToMono(T::class.java)
        .block()!!
}
