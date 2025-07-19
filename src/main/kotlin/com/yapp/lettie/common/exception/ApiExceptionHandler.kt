package com.yapp.lettie.common.exception

import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.error.analyzer.dto.AnalyzeErrorRequest
import com.yapp.lettie.common.error.reporter.LlmErrorReporter
import com.yapp.lettie.common.logging.RequestIdFilter
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class ApiExceptionHandler(
    private val llmErrorReporter: LlmErrorReporter,
) {
    private val log = KotlinLogging.logger {}

    @ExceptionHandler(ApiErrorException::class)
    fun handleApiErrorException(ex: ApiErrorException): ResponseEntity<ApiResponse<Nothing>> {
        log.error { "ApiErrorException occurred: ${ex.error.data}" }
        return ResponseEntity
            .status(ex.error.status.code)
            .body(ApiResponse.error(ex.error))
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNotFoundException(
        ex: NoHandlerFoundException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.warn { "NoHandlerFoundException: ${request.method} ${request.requestURI}" }
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ApiError.of(ErrorMessages.NOT_FOUND)))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowedException(
        ex: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.warn { "MethodNotAllowed: ${request.method} ${request.requestURI}" }
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(ApiResponse.error(ApiError.of(ErrorMessages.METHOD_NOT_ALLOWED)))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnknownException(
        ex: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val error = ApiError.of(ErrorMessages.INTERNAL_SERVER_ERROR)

        llmErrorReporter.report(
            AnalyzeErrorRequest(
                path = request.requestURI,
                httpMethod = request.method,
                exception = ex,
                // TODO: userId 넣기
                userId = null,
                notify = true,
                logId = MDC.get(RequestIdFilter.REQUEST_ID),
            ),
        )

        log.error(ex) { "처리되지 않은 서버 예외 발생" }

        return ResponseEntity
            .status(error.status.code)
            .body(ApiResponse.error(error))
    }
}
