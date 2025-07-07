package com.yapp.lettie.util

import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClient

@Component
class RestClientUtil(
    private val restClient: RestClient,
) {
    fun <T> get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        responseType: Class<T>,
    ): T? =
        restClient
            .get()
            .uri(url)
            .headers { httpHeaders ->
                applyHeaders(httpHeaders, headers)
            }.retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                handle4xxError(response.statusCode, response)
            }.onStatus(HttpStatusCode::is5xxServerError) { _, response ->
                handle5xxError(response.statusCode, response)
            }.body(responseType)

    fun <T> post(
        url: String,
        headers: Map<String, String> = emptyMap(),
        body: MultiValueMap<String, String>,
        responseType: Class<T>,
    ): T? =
        restClient
            .post()
            .uri(url)
            .headers { httpHeaders ->
                httpHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
                applyHeaders(httpHeaders, headers)
            }.body(body)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                handle4xxError(response.statusCode, response)
            }.onStatus(HttpStatusCode::is5xxServerError) { _, response ->
                handle5xxError(response.statusCode, response)
            }.body(responseType)

    fun <T> post(
        url: String,
        query: MultiValueMap<String, String>,
        responseType: Class<T>,
    ): T? =
        restClient
            .post()
            .uri(buildUrlWithQueryParams(url, query))
            .headers { httpHeaders ->
                httpHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
            }.retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                handle4xxError(response.statusCode, response)
            }.onStatus(HttpStatusCode::is5xxServerError) { _, response ->
                handle5xxError(response.statusCode, response)
            }.body(responseType)

    private fun applyHeaders(
        httpHeaders: org.springframework.http.HttpHeaders,
        headers: Map<String, String>,
    ) {
        headers.forEach { (key, value) -> httpHeaders.add(key, value) }
    }

    private fun handle4xxError(
        status: HttpStatusCode,
        response: ClientHttpResponse,
    ) {
        val message = buildErrorMessage("4xx", status, response)
        throw ApiErrorException(ErrorMessages.INVALID_INPUT_VALUE, data = message)
    }

    private fun handle5xxError(
        status: HttpStatusCode,
        response: ClientHttpResponse,
    ) {
        val message = buildErrorMessage("5xx", status, response)
        throw ApiErrorException(ErrorMessages.INTERNAL_SERVER_ERROR, data = message)
    }

    private fun buildErrorMessage(
        category: String,
        status: HttpStatusCode,
        response: ClientHttpResponse,
    ): String {
        val bodyText = response.body.bufferedReader().use { it.readText() }
        return "$category client error -> statusCode: ${status.value()}, Body: $bodyText, Headers: ${response.headers}"
    }

    private fun buildUrlWithQueryParams(
        baseUrl: String,
        params: MultiValueMap<String, String>,
    ): String {
        val queryParams =
            params.entries.joinToString("&") { (key, values) ->
                values.joinToString("&") { value -> "$key=$value" }
            }
        return "$baseUrl?$queryParams"
    }
}
