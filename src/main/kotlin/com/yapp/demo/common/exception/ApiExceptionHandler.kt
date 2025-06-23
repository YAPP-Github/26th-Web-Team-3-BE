package com.yapp.demo.common.exception

import com.yapp.demo.common.dto.ApiResponse
import com.yapp.demo.common.error.ErrorMessages
import com.yapp.demo.common.error.analyzer.dto.AnalyzeErrorRequest
import com.yapp.demo.common.error.reporter.LlmErrorReporter
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.UUID

@RestControllerAdvice
class ApiExceptionHandler(
    private val llmErrorReporter: LlmErrorReporter,
) {
    private val log = KotlinLogging.logger {}

    @ExceptionHandler(ApiErrorException::class)
    fun handleApiErrorException(ex: ApiErrorException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ex.error.status.code)
            .body(ApiResponse.error(ex.error))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnknownException(
        ex: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val error = ApiError.of(ErrorMessages.INTERNAL_SERVER_ERROR)

        // 예외 분석 및 Discord 알림 비동기 위임
        llmErrorReporter.report(
            AnalyzeErrorRequest(
                path = request.requestURI,
                httpMethod = request.method,
                exception = ex,
                // TODO: 로그인 사용자 주입
                userId = null,
                notify = true,
                logId = UUID.randomUUID().toString(),
            ),
        )

        log.error(ex) { "처리되지 않은 서버 예외 발생" }

        return ResponseEntity.status(error.status.code)
            .body(ApiResponse.error(error))
    }
}
