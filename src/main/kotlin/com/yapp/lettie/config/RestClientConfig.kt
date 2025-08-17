package com.yapp.lettie.config

import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.core5.util.Timeout
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {
    @Bean
    fun restClient(): RestClient =
        RestClient
            .builder()
            .requestFactory(customRequestFactory())
            .build()

    private fun customRequestFactory(): ClientHttpRequestFactory {
        val connectionConfig =
            ConnectionConfig
                .custom()
                .setConnectTimeout(Timeout.ofSeconds(3))
                .setSocketTimeout(Timeout.ofSeconds(3))
                .build()

        val requestConfig =
            RequestConfig
                .custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(3))
                .setResponseTimeout(Timeout.ofSeconds(3))
                .build()

        val connectionManager =
            PoolingHttpClientConnectionManager().apply {
                defaultMaxPerRoute = 150
                maxTotal = 150
                setDefaultConnectionConfig(connectionConfig)
            }

        val httpClient: CloseableHttpClient =
            HttpClientBuilder
                .create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build()

        return HttpComponentsClientHttpRequestFactory(httpClient)
    }
}
