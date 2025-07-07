package com.yapp.lettie.common.exception

import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.error.reporter.LlmErrorReporter
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

class ApiExceptionHandlerTest {
    private lateinit var reporter: LlmErrorReporter
    private lateinit var handler: ApiExceptionHandler

    @BeforeEach
    fun setUp() {
        reporter = mockk(relaxed = true)
        handler = ApiExceptionHandler(reporter)
    }

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
    fun `Unknown Exception이 발생하면 LlmErrorReporter가 호출되고 500 응답을 반환해야 한다`() {
        // given
        val ex = RuntimeException("DB 연결 실패")
        val mockRequest =
            mockk<HttpServletRequest> {
                every { requestURI } returns "/api/test"
                every { method } returns "POST"
            }

        // when
        val response: ResponseEntity<ApiResponse<Nothing>> = handler.handleUnknownException(ex, mockRequest)

        // then
        assertThat(response.statusCode.value()).isEqualTo(500)
        assertThat(response.body?.error?.status?.name).isEqualTo("INTERNAL_SERVER_ERROR")
        assertThat(response.body?.error?.code).isEqualTo("500")
        assertThat(response.body?.error?.message).isEqualTo("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.")

        // LLM 분석 요청 확인
        verify(exactly = 1) {
            reporter.report(
                withArg {
                    assertThat(it.path).isEqualTo("/api/test")
                    assertThat(it.httpMethod).isEqualTo("POST")
                    assertThat(it.exception).isEqualTo(ex)
                    assertThat(it.notify).isTrue()
                    assertThat(it.userId).isNull()
                },
            )
        }
    }
}
