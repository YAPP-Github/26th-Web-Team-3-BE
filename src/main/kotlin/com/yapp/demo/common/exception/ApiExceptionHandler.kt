package com.yapp.demo.common.exception

import com.yapp.demo.common.dto.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(ApiErrorException::class)
    fun handleApiErrorException(ex: ApiErrorException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ex.error.status.code)
            .body(ApiResponse.error(ex.error))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnknownException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val error = ApiError.of(
            status = ExtendedHttpStatus.INTERNAL_SERVER_ERROR,
            code = "internal.error"
        )
        return ResponseEntity.status(ExtendedHttpStatus.INTERNAL_SERVER_ERROR.code).body(ApiResponse.error(error))
    }
}
