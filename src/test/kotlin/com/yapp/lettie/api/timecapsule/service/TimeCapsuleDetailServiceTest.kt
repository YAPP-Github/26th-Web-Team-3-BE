package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.user.service.reader.UserReader
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleLike
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import com.yapp.lettie.domain.user.entity.User
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
class TimeCapsuleDetailServiceTest {
    @MockK
    lateinit var userReader: UserReader

    @MockK
    lateinit var capsuleReader: TimeCapsuleReader

    @MockK
    lateinit var likeReader: TimeCapsuleLikeReader

    @InjectMockKs
    lateinit var detailService: TimeCapsuleDetailService

    @Test
    fun `캡슐이 WRITABLE 상태일 때, 상세 정보를 정확히 반환한다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val capsuleId = 1L
        val userId = 10L

        val user = mockk<User>()
        val users = listOf(mockk<TimeCapsuleUser>(), mockk())
        val likes =
            listOf(
                mockk<TimeCapsuleLike> {
                    every { isLiked } returns true
                },
            )

        val capsule =
            TimeCapsule(
                id = capsuleId,
                inviteCode = "ABC123",
                title = "캡슐 제목",
                subtitle = "부제목",
                accessType = AccessType.PUBLIC,
                openAt = now.plusDays(3),
                closedAt = now.plusDays(2),
            ).apply {
                timeCapsuleUsers.addAll(users)
                timeCapsuleLikes.addAll(likes)
            }

        every { capsuleReader.getById(capsuleId) } returns capsule
        every { likeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns
            mockk {
                every { isLiked } returns true
            }

        // when
        val result = detailService.getTimeCapsuleDetail(capsuleId, userId)

        // then
        assertEquals(capsuleId, result.id)
        assertEquals("캡슐 제목", result.title)
        assertEquals(TimeCapsuleStatus.WRITABLE, result.status)
        assertTrue(result.isLiked!!)
        assertEquals(1, result.likeCount)
        assertEquals(2, result.participantCount)
        assertNotNull(result.remainingTime)
        assertEquals(1, result.remainingTime?.days)
    }

    @Test
    fun `캡슐이 WAITING_OPEN 상태일 때, 상세 정보를 정확히 반환한다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val userId = 2L
        val capsuleId = 20L
        val users = listOf(mockk<TimeCapsuleUser>())
        val likes =
            listOf(
                mockk<TimeCapsuleLike> {
                    every { isLiked } returns true
                },
            )

        val capsule =
            TimeCapsule(
                id = capsuleId,
                inviteCode = "WAIT123",
                title = "대기 중 캡슐",
                subtitle = "기다림의 미학",
                accessType = AccessType.PUBLIC,
                openAt = now.plusDays(3),
                closedAt = now.minusDays(2),
            ).apply {
                timeCapsuleUsers.addAll(users)
                timeCapsuleLikes.addAll(likes)
            }

        every { capsuleReader.getById(capsuleId) } returns capsule
        every { likeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns
            mockk {
                every { isLiked } returns true
            }

        // when
        val result = detailService.getTimeCapsuleDetail(capsuleId, userId)

        // then
        assertEquals(TimeCapsuleStatus.WAITING_OPEN, result.status)
        assertNotNull(result.remainingTime)
        assertEquals(2, result.remainingTime?.days)
        assertEquals(23, result.remainingTime?.hours)
        assertEquals(59, result.remainingTime?.minutes)
    }

    @Test
    fun `캡슐이 OPENED 상태일 때, 상세 정보를 정확히 반환한다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val capsuleId = 30L
        val userId = 1L
        val users = listOf(mockk<TimeCapsuleUser>())
        val likes =
            listOf(
                mockk<TimeCapsuleLike> {
                    every { isLiked } returns false
                },
            )

        val capsule =
            TimeCapsule(
                id = capsuleId,
                inviteCode = "OPEN123",
                title = "오픈된 캡슐",
                subtitle = "이제 읽어보세요",
                accessType = AccessType.PUBLIC,
                openAt = now.minusDays(2),
                closedAt = now.minusDays(3),
            ).apply {
                timeCapsuleUsers.addAll(users)
                timeCapsuleLikes.addAll(likes)
            }

        every { capsuleReader.getById(capsuleId) } returns capsule
        every { likeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns
            mockk {
                every { isLiked } returns true
            }

        // when
        val result = detailService.getTimeCapsuleDetail(capsuleId, userId)

        // then
        assertEquals(TimeCapsuleStatus.OPENED, result.status)
        assertEquals(now.minusDays(2).toLocalDate(), result.remainingTime?.openDate)
    }
}
