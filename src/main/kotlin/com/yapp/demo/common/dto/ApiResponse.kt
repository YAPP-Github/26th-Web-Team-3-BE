package com.yapp.demo.common.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.yapp.demo.common.exception.ApiError
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    @get:JsonProperty("result")
    val result: T? = null,

    @get:JsonProperty("error")
    @Schema(hidden = true)
    val error: ApiError? = null
) {
    @get:JsonIgnore
    val isSuccess: Boolean get() = error == null

    companion object {
        fun <T> success(result: T): ApiResponse<T> = ApiResponse(result = result)
        fun <T> error(error: ApiError): ApiResponse<T> = ApiResponse(error = error)
    }
}
