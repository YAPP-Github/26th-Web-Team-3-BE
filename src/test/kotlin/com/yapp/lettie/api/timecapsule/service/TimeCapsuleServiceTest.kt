package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsulePayload
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleUserReader
import com.yapp.lettie.api.timecapsule.service.writer.TimeCapsuleLikeWriter
import com.yapp.lettie.api.timecapsule.service.writer.TimeCapsuleWriter
import com.yapp.lettie.api.user.service.reader.UserReader
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleLike
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleUserStatus
import com.yapp.lettie.domain.user.entity.User
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class TimeCapsuleServiceTest {
    @MockK lateinit var userReader: UserReader

    @MockK lateinit var capsuleWriter: TimeCapsuleWriter

    @MockK lateinit var capsuleReader: TimeCapsuleReader

    @MockK lateinit var capsuleLikeWriter: TimeCapsuleLikeWriter

    @MockK lateinit var capsuleLikeReader: TimeCapsuleLikeReader

    @MockK lateinit var capsuleUserReader: TimeCapsuleUserReader

    @InjectMockKs
    lateinit var timeCapsuleService: TimeCapsuleService

    @Test
    fun `타임캡슐을 생성하면 저장소에 저장되고 dto를 반환한다`() {
        // given
        val userId = 1L
        val user = mockk<User>(relaxed = true)
        val payload =
            CreateTimeCapsulePayload(
                title = "title",
                subtitle = "sub",
                accessType = AccessType.PRIVATE,
                openAt = LocalDateTime.now().plusDays(10),
                closedAt = LocalDateTime.now(),
            )

        val dummyCapsule =
            mockk<TimeCapsule> {
                every { id } returns 123L
                every { inviteCode } returns "abcd1234"
            }

        every { userReader.getById(userId) } returns user
        every { capsuleWriter.save(any()) } returns dummyCapsule

        // when
        val result = timeCapsuleService.createTimeCapsule(userId, payload)

        // then
        verify { capsuleWriter.save(any()) }
        assertThat(result.id).isEqualTo(123L)
        assertThat(result.inviteCode).isEqualTo("abcd1234")
    }

    @Test
    fun `좋아요가 없으면 새로 생성된다`() {
        // given
        val userId = 1L
        val capsuleId = 10L
        val user = mockk<User>()
        val capsule = mockk<TimeCapsule>()
        val likeSlot = slot<TimeCapsuleLike>()

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsuleLikeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns null
        every { capsuleLikeWriter.save(capture(likeSlot)) } answers { likeSlot.captured }

        // when
        timeCapsuleService.like(userId, capsuleId)

        // then
        assertThat(likeSlot.isCaptured).isTrue()
        assertThat(likeSlot.captured.isLiked).isTrue()
        assertThat(likeSlot.captured.user).isEqualTo(user)
        assertThat(likeSlot.captured.timeCapsule).isEqualTo(capsule)
    }

    @Test
    fun `좋아요가 취소된 상태면 다시 true로 바뀐다`() {
        // given
        val userId = 1L
        val capsuleId = 20L
        val user = mockk<User>()
        val capsule = mockk<TimeCapsule>()
        val existingLike = spyk(TimeCapsuleLike.of(user, capsule))
        existingLike.isLiked = false

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsuleLikeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns existingLike
        every { capsuleLikeWriter.save(existingLike) } returns existingLike

        // when
        timeCapsuleService.like(userId, capsuleId)

        // then
        assertThat(existingLike.isLiked).isTrue()
        verify { capsuleLikeWriter.save(existingLike) }
    }

    @Test
    fun `이미 좋아요한 상태에서 좋아요를 누르면 저장되지 않는다`() {
        // given
        val userId = 1L
        val capsuleId = 20L
        val user = mockk<User>()
        val capsule = mockk<TimeCapsule>()
        val existingLike = spyk(TimeCapsuleLike.of(user, capsule))
        existingLike.isLiked = true

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsuleLikeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns existingLike

        // when
        timeCapsuleService.like(userId, capsuleId)

        // then
        verify(exactly = 0) { capsuleLikeWriter.save(any()) }
    }

    @Test
    fun `좋아요 취소시 기존 좋아요가 있으면 false로 변경된다`() {
        // given
        val userId = 1L
        val capsuleId = 20L
        val user = mockk<User>()
        val capsule = mockk<TimeCapsule>()
        val existingLike = spyk(TimeCapsuleLike.of(user, capsule))
        existingLike.isLiked = true

        every { capsuleLikeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns existingLike
        every { capsuleLikeWriter.save(existingLike) } returns existingLike

        // when
        timeCapsuleService.unlike(userId, capsuleId)

        // then
        assertThat(existingLike.isLiked).isFalse()
        verify { capsuleLikeWriter.save(existingLike) }
    }

    @Test
    fun `좋아요 취소시 기존 좋아요가 없으면 아무것도 하지 않는다`() {
        // given
        val userId = 1L
        val capsuleId = 20L

        every { capsuleLikeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns null

        // when
        timeCapsuleService.unlike(userId, capsuleId)

        // then
        verify(exactly = 0) { capsuleLikeWriter.save(any()) }
    }

    @Test
    fun `좋아요 취소시 이미 취소된 상태면 아무것도 하지 않는다`() {
        // given
        val userId = 1L
        val capsuleId = 20L
        val user = mockk<User>()
        val capsule = mockk<TimeCapsule>()
        val existingLike = spyk(TimeCapsuleLike.of(user, capsule))
        existingLike.isLiked = false

        every { capsuleLikeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns existingLike

        // when
        timeCapsuleService.unlike(userId, capsuleId)

        // then
        verify(exactly = 0) { capsuleLikeWriter.save(any()) }
    }

    @Test
    fun `타임캡슐 생성시 openAt이 현재시각보다 이전이면 예외가 발생한다`() {
        // given
        val userId = 1L
        val user = mockk<User>(relaxed = true)
        val payload = CreateTimeCapsulePayload(
            title = "title",
            subtitle = "sub",
            accessType = AccessType.PRIVATE,
            openAt = LocalDateTime.now().minusDays(1), // 과거 시간
            closedAt = LocalDateTime.now(),
        )

        every { userReader.getById(userId) } returns user

        // when & then
        val exception = assertThrows<ApiErrorException> {
            timeCapsuleService.createTimeCapsule(userId, payload)
        }

        assertThat(exception.error.message).isEqualTo(ErrorMessages.INVALID_OPEN_AT.message)
    }

    @Test
    fun `타임캡슐 생성시 closedAt이 openAt보다 늦으면 예외가 발생한다`() {
        // given
        val userId = 1L
        val user = mockk<User>(relaxed = true)
        val payload = CreateTimeCapsulePayload(
            title = "title",
            subtitle = "sub",
            accessType = AccessType.PRIVATE,
            openAt = LocalDateTime.now().plusDays(5),
            closedAt = LocalDateTime.now().plusDays(10), // openAt보다 늦음
        )

        every { userReader.getById(userId) } returns user

        // when & then
        val exception = assertThrows<ApiErrorException> {
            timeCapsuleService.createTimeCapsule(userId, payload)
        }

        assertThat(exception.error.message).isEqualTo(ErrorMessages.INVALID_CLOSED_AT.message)
    }

    @Test
    fun `타임캡슐 떠나기가 성공하면 사용자가 비활성화된다`() {
        // given
        val userId = 1L
        val capsuleId = 10L
        val user = mockk<User> { every { id } returns userId }
        val timeCapsuleUser = spyk(TimeCapsuleUser.of(user, mockk(relaxed = true)))
        val capsule = mockk<TimeCapsule> {
            every { timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)
        }

        every { capsuleReader.getById(capsuleId) } returns capsule

        // when
        timeCapsuleService.leaveTimeCapsule(userId, capsuleId)

        // then
        verify { timeCapsuleUser.leave() }
    }

    @Test
    fun `참여하지 않은 타임캡슐에서 떠나기를 시도하면 예외가 발생한다`() {
        // given
        val userId = 1L
        val capsuleId = 10L
        val capsule = mockk<TimeCapsule> {
            every { timeCapsuleUsers } returns mutableListOf()
        }

        every { capsuleReader.getById(capsuleId) } returns capsule

        // when & then
        val exception = assertThrows<ApiErrorException> {
            timeCapsuleService.leaveTimeCapsule(userId, capsuleId)
        }

        assertThat(exception.error.message).isEqualTo(ErrorMessages.NOT_JOINED_CAPSULE.message)
    }

    @Test
    fun `이미 떠난 타임캡슐에서 다시 떠나기를 시도하면 예외가 발생한다`() {
        // given
        val userId = 1L
        val capsuleId = 10L
        val user = mockk<User> { every { id } returns userId }
        val timeCapsuleUser = spyk(TimeCapsuleUser.of(user, mockk(relaxed = true)))
        timeCapsuleUser.leave() // 이미 떠난 상태
        val capsule = mockk<TimeCapsule> {
            every { timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)
        }

        every { capsuleReader.getById(capsuleId) } returns capsule

        // when & then
        val exception = assertThrows<ApiErrorException> {
            timeCapsuleService.leaveTimeCapsule(userId, capsuleId)
        }

        assertThat(exception.error.message).isEqualTo(ErrorMessages.NOT_JOINED_CAPSULE.message)
    }

    @Test
    fun `타임캡슐을 처음 열면 isFirstOpen이 true로 반환된다`() {
        // given
        val capsuleId = 1L
        val userId = 10L
        val timeCapsuleUser = spyk(TimeCapsuleUser.of(mockk(relaxed = true), mockk(relaxed = true)))
        timeCapsuleUser.isOpened = false

        every { capsuleUserReader.findTimeCapsuleUser(capsuleId, userId) } returns timeCapsuleUser

        // when
        val result = timeCapsuleService.openTimeCapsule(capsuleId, userId)

        // then
        assertThat(result.isFirstOpen).isTrue()
        verify { timeCapsuleUser.updateOpened() }
    }

    @Test
    fun `이미 열린 타임캡슐을 다시 열면 isFirstOpen이 false로 반환된다`() {
        // given
        val capsuleId = 1L
        val userId = 10L
        val timeCapsuleUser = spyk(TimeCapsuleUser.of(mockk(relaxed = true), mockk(relaxed = true)))
        timeCapsuleUser.isOpened = true

        every { capsuleUserReader.findTimeCapsuleUser(capsuleId, userId) } returns timeCapsuleUser

        // when
        val result = timeCapsuleService.openTimeCapsule(capsuleId, userId)

        // then
        assertThat(result.isFirstOpen).isFalse()
        verify(exactly = 0) { timeCapsuleUser.updateOpened() }
    }

    @Test
    fun `userId가 null이면 isFirstOpen이 false로 반환된다`() {
        // given
        val capsuleId = 1L

        // when
        val result = timeCapsuleService.openTimeCapsule(capsuleId, null)

        // then
        assertThat(result.isFirstOpen).isFalse()
        verify(exactly = 0) { capsuleUserReader.findTimeCapsuleUser(any(), any()) }
    }

    @Test
    fun `참여하지 않은 사용자가 타임캡슐을 열면 isFirstOpen이 false로 반환된다`() {
        // given
        val capsuleId = 1L
        val userId = 10L

        every { capsuleUserReader.findTimeCapsuleUser(capsuleId, userId) } returns null

        // when
        val result = timeCapsuleService.openTimeCapsule(capsuleId, userId)

        // then
        assertThat(result.isFirstOpen).isFalse()
    }
}
