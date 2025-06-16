package com.yapp.demo.common.exception

import com.yapp.demo.common.dto.ApiResponse
import com.yapp.demo.common.error.ErrorMessages
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

class ApiExceptionHandlerTest {

    private val handler = ApiExceptionHandler()

    @Test
    fun `ApiErrorException이 발생하면 ApiError 형식의 응답을 반환해야 한다`() {
        // given
        val exception = ApiErrorException(ErrorMessages.INVALID_INPUT_VALUE)

        // when
        val response: ResponseEntity<ApiResponse<Nothing>> = handler.handleApiErrorException(exception)

        // then
        assertThat(response.statusCode.value()).isEqualTo(400)
        assertThat(response.body?.error?.status?.name).isEqualTo("BAD_REQUEST")
        assertThat(response.body?.error?.code).isEqualTo("400")
        assertThat(response.body?.error?.message).isEqualTo("입력값이 올바르지 않습니다.")
        assertThat(response.body?.error?.data).isNull()
    }

    @Test
    fun `Unknown Exception이 발생하면 INTERNAL_SERVER_ERROR 응답을 반환해야 한다`() {
        // given
        val unknownException = RuntimeException("DB 연결 실패")

        // when
        val response: ResponseEntity<ApiResponse<Nothing>> = handler.handleUnknownException(unknownException)

        // then
        assertThat(response.statusCode.value()).isEqualTo(500)
        assertThat(response.body?.error?.status?.name).isEqualTo("INTERNAL_SERVER_ERROR")
        assertThat(response.body?.error?.code).isEqualTo("500")
        assertThat(response.body?.error?.message).isEqualTo("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.")
    }
}
