package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsulePayload
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
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
    fun `타임캡슐에 처음 참여하는 유저는 정상적으로 참여된다`() {
        // given
        val userId = 1L
        val capsuleId = 99L
        val user = mockk<User>(relaxed = true) { every { id } returns userId }
        val capsule =
            mockk<TimeCapsule>(relaxed = true) {
                every { timeCapsuleUsers } returns mutableListOf()
                every { closedAt } returns LocalDateTime.now().plusDays(1)
            }

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule

        // when
        timeCapsuleService.joinTimeCapsule(userId, capsuleId)

        // then
        verify { capsule.addUser(any()) }
        verify { user.addTimeCapsuleUser(any()) }
    }

    @Test
    fun `이미 참여한 유저가 다시 참여하면 예외가 발생한다`() {
        // given
        val userId = 1L
        val capsuleId = 99L
        val user = mockk<User> { every { id } returns userId }
        val capsule = mockk<TimeCapsule>(relaxed = true)

        val existing = TimeCapsuleUser.of(user, capsule)

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsule.timeCapsuleUsers } returns mutableListOf(existing)
        every { capsule.closedAt } returns LocalDateTime.now().plusDays(1)

        // when
        val exception =
            assertThrows<ApiErrorException> {
                timeCapsuleService.joinTimeCapsule(userId, capsuleId)
            }

        // then
        assertThat(exception.error.message).isEqualTo(ErrorMessages.ALREADY_JOINED.message)
    }

    @Test
    fun `closedAt을 지난 타임캡슐에 참여하면 예외가 발생한다`() {
        // given
        val userId = 1L
        val capsuleId = 100L
        val user = mockk<User> { every { id } returns userId }
        val capsule =
            mockk<TimeCapsule> {
                every { timeCapsuleUsers } returns mutableListOf()
                every { isClosed(any()) } returns true
            }

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule

        // when & then
        val exception =
            assertThrows<ApiErrorException> {
                timeCapsuleService.joinTimeCapsule(userId, capsuleId)
            }

        // then
        assertThat(exception.error.message).isEqualTo(ErrorMessages.CLOSED_TIME_CAPSULE.message)
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
    fun `좋아요가 이미 되어 있는 상태에서 unlike를 하면 false로 변경된다`() {
        // given
        val userId = 1L
        val capsuleId = 30L
        val user = mockk<User>()
        val capsule = mockk<TimeCapsule>()
        val existingLike = spyk(TimeCapsuleLike.of(user, capsule))
        existingLike.isLiked = true

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsuleLikeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns existingLike
        every { capsuleLikeWriter.save(existingLike) } returns existingLike

        // when
        timeCapsuleService.unlike(userId, capsuleId)

        // then
        assertThat(existingLike.isLiked).isFalse()
        verify { capsuleLikeWriter.save(existingLike) }
    }

    @Test
    fun `활성 상태인 사용자가 캡슐을 나가면 상태가 LEFT로 변경된다`() {
        // given
        val userId = 1L
        val capsuleId = 40L
        val user = mockk<User> { every { id } returns userId }
        val capsule = mockk<TimeCapsule>(relaxed = true)
        val timeCapsuleUser = spyk(TimeCapsuleUser.of(user, capsule))

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)

        // when
        timeCapsuleService.leaveTimeCapsule(userId, capsuleId)

        // then
        assertThat(timeCapsuleUser.status).isEqualTo(TimeCapsuleUserStatus.LEFT)
        verify { timeCapsuleUser.leave() }
    }

    @Test
    fun `참여하지 않은 사용자가 캡슐을 나가려고 하면 예외가 발생한다`() {
        // given
        val userId = 1L
        val capsuleId = 50L
        val user = mockk<User> { every { id } returns userId }
        val capsule = mockk<TimeCapsule>(relaxed = true)

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsule.timeCapsuleUsers } returns mutableListOf()

        // when & then
        val exception =
            assertThrows<ApiErrorException> {
                timeCapsuleService.leaveTimeCapsule(userId, capsuleId)
            }

        assertThat(exception.error.message).isEqualTo(ErrorMessages.NOT_JOINED_CAPSULE.message)
    }

    @Test
    fun `이미 나간 사용자가 다시 나가려고 하면 예외가 발생한다`() {
        // given
        val userId = 1L
        val capsuleId = 60L
        val user = mockk<User> { every { id } returns userId }
        val capsule = mockk<TimeCapsule>(relaxed = true)
        val timeCapsuleUser = spyk(TimeCapsuleUser.of(user, capsule))
        timeCapsuleUser.status = TimeCapsuleUserStatus.LEFT

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)

        // when & then
        val exception =
            assertThrows<ApiErrorException> {
                timeCapsuleService.leaveTimeCapsule(userId, capsuleId)
            }

        assertThat(exception.error.message).isEqualTo(ErrorMessages.NOT_JOINED_CAPSULE.message)
    }

    @Test
    fun `나간 사용자가 있는 캡슐에서 활성 사용자만 참여 체크된다`() {
        // given
        val userId1 = 1L
        val userId2 = 2L
        val capsuleId = 70L
        val user1 = mockk<User> { every { id } returns userId1 }
        val user2 = mockk<User> { every { id } returns userId2 }
        val capsule = mockk<TimeCapsule>(relaxed = true)

        val activeUser = spyk(TimeCapsuleUser.of(user1, capsule)) // ACTIVE 상태
        val leftUser = spyk(TimeCapsuleUser.of(user2, capsule))
        leftUser.status = TimeCapsuleUserStatus.LEFT // LEFT 상태

        every { userReader.getById(userId1) } returns user1
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsule.timeCapsuleUsers } returns mutableListOf(activeUser, leftUser)
        every { capsule.closedAt } returns LocalDateTime.now().plusDays(1)

        // when & then - 활성 사용자는 이미 참여한 것으로 인식
        val exception =
            assertThrows<ApiErrorException> {
                timeCapsuleService.joinTimeCapsule(userId1, capsuleId)
            }
        assertThat(exception.error.message).isEqualTo(ErrorMessages.ALREADY_JOINED.message)
    }

    @Test
    fun `LEFT 상태인 사용자는 다시 참여할 수 있다`() {
        // given
        val userId = 1L
        val capsuleId = 80L
        val user = mockk<User>(relaxed = true) { every { id } returns userId }
        val capsule = mockk<TimeCapsule>(relaxed = true)
        val timeCapsuleUser = spyk(TimeCapsuleUser.of(user, capsule))
        timeCapsuleUser.status = TimeCapsuleUserStatus.LEFT

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)
        every { capsule.closedAt } returns LocalDateTime.now().plusDays(1)

        // when
        timeCapsuleService.joinTimeCapsule(userId, capsuleId)

        // then
        verify { capsule.addUser(any()) }
        verify { user.addTimeCapsuleUser(any()) }
    }
}
