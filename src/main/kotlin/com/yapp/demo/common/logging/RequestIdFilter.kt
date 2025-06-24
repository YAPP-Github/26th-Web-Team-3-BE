package com.yapp.demo.common.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import java.util.UUID

@Component
class RequestIdFilter : GenericFilterBean() {
    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain,
    ) {
        try {
            val requestId = UUID.randomUUID().toString()
            MDC.put(REQUEST_ID, requestId)
            chain.doFilter(request, response)
        } finally {
            MDC.remove(REQUEST_ID)
        }
    }

    companion object {
        const val REQUEST_ID = "requestId"
    }
}
