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
import com.yapp.lettie.domain.user.entity.User
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class TimeCapsuleServiceTest {
    private val userReader: UserReader = mockk()
    private val capsuleWriter: TimeCapsuleWriter = mockk()
    private val capsuleReader: TimeCapsuleReader = mockk()
    private val capsuleLikeWriter: TimeCapsuleLikeWriter = mockk()
    private val capsuleLikeReader: TimeCapsuleLikeReader = mockk()

    private lateinit var timeCapsuleService: TimeCapsuleService

    @BeforeEach
    fun setUp() {
        timeCapsuleService =
            TimeCapsuleService(userReader, capsuleWriter, capsuleReader, capsuleLikeWriter, capsuleLikeReader)
    }

    @Test
    fun `타임캡슐을 생성하면 저장소에 저장된다`() {
        // given
        val userId = 1L
        val user = mockk<User>(relaxed = true)
        val payload =
            CreateTimeCapsulePayload(
                title = "title",
                subtitle = "sub",
                accessType = AccessType.PRIVATE,
                openAt = LocalDateTime.now(),
                closedAt = LocalDateTime.now().plusDays(10),
            )

        val dummyCapsule =
            mockk<TimeCapsule> {
                every { id } returns 123L
            }

        every { userReader.getById(userId) } returns user
        every { capsuleWriter.save(any()) } returns dummyCapsule

        // when
        val capsuleId = timeCapsuleService.createTimeCapsule(userId, payload)

        // then
        verify { capsuleWriter.save(any()) }
        assertThat(capsuleId).isEqualTo(123L)
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
                every { closedAt } returns LocalDateTime.now().minusMinutes(1)
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
    fun `좋아요가 없으면 새로 생성되고 true가 반환된다`() {
        // given
        val userId = 1L
        val capsuleId = 10L
        val user = mockk<User>()
        val capsule = mockk<TimeCapsule>()
        val likeSlot = slot<TimeCapsuleLike>()

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsuleLikeReader.findByUserAndCapsule(user, capsule) } returns null
        every { capsuleLikeWriter.save(capture(likeSlot)) } answers { likeSlot.captured }

        // when
        val result = timeCapsuleService.toggleLike(userId, capsuleId)

        // then
        assertThat(result).isTrue()
        assertThat(likeSlot.isCaptured).isTrue()
        assertThat(likeSlot.captured.isLiked).isTrue()
        assertThat(likeSlot.captured.user).isEqualTo(user)
        assertThat(likeSlot.captured.timeCapsule).isEqualTo(capsule)
    }

    @Test
    fun `좋아요가 이미 되어 있으면 isLiked를 false로 변경하고 false를 반환한다`() {
        // given
        val userId = 1L
        val capsuleId = 20L
        val user = mockk<User>()
        val capsule = mockk<TimeCapsule>()
        val existingLike = spyk(TimeCapsuleLike.of(user, capsule))
        existingLike.isLiked = true

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsuleLikeReader.findByUserAndCapsule(user, capsule) } returns existingLike
        every { capsuleLikeWriter.save(existingLike) } returns existingLike

        // when
        val result = timeCapsuleService.toggleLike(userId, capsuleId)

        // then
        assertThat(result).isFalse()
        assertThat(existingLike.isLiked).isFalse()
        verify { capsuleLikeWriter.save(existingLike) }
    }

    @Test
    fun `좋아요가 취소된 상태면 다시 true로 바꾸고 true를 반환한다`() {
        // given
        val userId = 1L
        val capsuleId = 30L
        val user = mockk<User>()
        val capsule = mockk<TimeCapsule>()
        val existingLike = spyk(TimeCapsuleLike.of(user, capsule))
        existingLike.isLiked = false

        every { userReader.getById(userId) } returns user
        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsuleLikeReader.findByUserAndCapsule(user, capsule) } returns existingLike
        every { capsuleLikeWriter.save(existingLike) } returns existingLike

        // when
        val result = timeCapsuleService.toggleLike(userId, capsuleId)

        // then
        assertThat(result).isTrue()
        assertThat(existingLike.isLiked).isTrue()
        verify { capsuleLikeWriter.save(existingLike) }
    }
}
