package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsulePayload
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.writer.TimeCapsuleWriter
import com.yapp.lettie.api.user.service.reader.UserReader
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import com.yapp.lettie.domain.user.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime

class TimeCapsuleServiceTest {
    private val userReader: UserReader = mock()
    private val capsuleWriter: TimeCapsuleWriter = mock()
    private val capsuleReader: TimeCapsuleReader = mock()

    private lateinit var timeCapsuleService: TimeCapsuleService

    @BeforeEach
    fun setUp() {
        timeCapsuleService = TimeCapsuleService(userReader, capsuleWriter, capsuleReader)
    }

    @Test
    fun `타임캡슐을 생성하면 저장소에 저장된다`() {
        // given
        val userId = 1L
        val user = mock<User>()
        val payload =
            CreateTimeCapsulePayload(
                title = "title",
                subtitle = "sub",
                accessType = AccessType.PRIVATE,
                openAt = LocalDateTime.now(),
                closedAt = LocalDate.now().plusDays(10),
            )

        whenever(userReader.getById(userId)).thenReturn(user)

        // when
        timeCapsuleService.createTimeCapsule(userId, payload)

        // then
        verify(capsuleWriter).save(any())
    }

    @Test
    fun `타임캡슐에 처음 참여하는 유저는 정상적으로 참여된다`() {
        // given
        val userId = 1L
        val capsuleId = 99L
        val user = mock<User> { on { id } doReturn userId }
        val capsule =
            mock<TimeCapsule> {
                on { timeCapsuleUsers } doReturn mutableListOf()
            }

        whenever(userReader.getById(userId)).thenReturn(user)
        whenever(capsuleReader.getById(capsuleId)).thenReturn(capsule)

        // when
        timeCapsuleService.joinTimeCapsule(userId, capsuleId)

        // then
        verify(capsule).addUser(any())
        verify(user).addTimeCapsuleUser(any())
    }

    @Test
    fun `이미 참여한 유저가 다시 참여하면 예외가 발생한다`() {
        // given
        val userId = 1L
        val capsuleId = 99L
        val user = mock<User> { on { id } doReturn userId }
        val existing = mock<TimeCapsuleUser> { on { this.user } doReturn user }

        val capsule =
            mock<TimeCapsule> {
                on { timeCapsuleUsers } doReturn mutableListOf(existing)
            }

        whenever(userReader.getById(userId)).thenReturn(user)
        whenever(capsuleReader.getById(capsuleId)).thenReturn(capsule)

        // when & then
        val exception =
            assertThrows<ApiErrorException> {
                timeCapsuleService.joinTimeCapsule(userId, capsuleId)
            }

        assertThat(exception.error.message).isEqualTo(ErrorMessages.ALREADY_JOINED.message)
    }
}
