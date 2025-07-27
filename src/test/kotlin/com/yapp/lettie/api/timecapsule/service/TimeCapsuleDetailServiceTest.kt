package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.letter.service.reader.LetterReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleUserReader
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

    @MockK
    lateinit var timeCapsuleUserReader: TimeCapsuleUserReader

    @MockK
    lateinit var letterReader: LetterReader

    @InjectMockKs
    lateinit var detailService: TimeCapsuleDetailService

    @Test
    fun `캡슐이 WRITABLE 상태일 때, 상세 정보를 정확히 반환한다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val capsuleId = 1L
        val userId = 10L

        val user =
            mockk<User> {
                every { id } returns userId
            }
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
                creator = user,
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
        every { likeReader.getLikeCount(capsuleId) } returns 1
        every { timeCapsuleUserReader.getParticipantCount(capsuleId) } returns 2
        every { letterReader.getLetterCountByCapsuleId(capsuleId) } returns 3

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

        val user =
            mockk<User> {
                every { id } returns userId
            }
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
                creator = user,
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
        every { likeReader.getLikeCount(capsuleId) } returns 1
        every { timeCapsuleUserReader.getParticipantCount(capsuleId) } returns 2
        every { letterReader.getLetterCountByCapsuleId(capsuleId) } returns 3

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

        val user =
            mockk<User> {
                every { id } returns userId
            }
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
                creator = user,
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
        every { likeReader.getLikeCount(capsuleId) } returns 1
        every { timeCapsuleUserReader.getParticipantCount(capsuleId) } returns 2
        every { letterReader.getLetterCountByCapsuleId(capsuleId) } returns 3

        // when
        val result = detailService.getTimeCapsuleDetail(capsuleId, userId)

        // then
        assertEquals(TimeCapsuleStatus.OPENED, result.status)
        assertEquals(now.minusDays(2).toLocalDate(), result.remainingTime?.openDate)
    }

    @Test
    fun `내 캡슐 목록을 가져올 때 요약 정보들이 정확히 반환된다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val userId = 1L

        val capsules = listOf(
            TimeCapsule(
                id = 1L,
                creator = mockk { every { id } returns userId },
                inviteCode = "CODE1",
                title = "내 첫 캡슐",
                subtitle = "서브1",
                accessType = AccessType.PUBLIC,
                openAt = now.plusDays(3),
                closedAt = now.plusDays(1)
            ),
            TimeCapsule(
                id = 2L,
                creator = mockk { every { id } returns userId },
                inviteCode = "CODE2",
                title = "내 두 번째 캡슐",
                subtitle = "서브2",
                accessType = AccessType.PUBLIC,
                openAt = now.plusDays(5),
                closedAt = now.plusDays(2)
            )
        )

        every { capsuleReader.getMyTimeCapsules(userId, any()) } returns capsules
        every { timeCapsuleUserReader.getParticipantCountMap(listOf(1L, 2L)) } returns mapOf(1L to 3, 2L to 5)
        every { letterReader.getLetterCountMap(listOf(1L, 2L)) } returns mapOf(1L to 10, 2L to 15)

        // when
        val result = detailService.getMyTimeCapsules(userId, limit = 2)

        // then
        assertEquals(2, result.size)
        val first = result[0]
        assertEquals(1L, first.id)
        assertEquals("내 첫 캡슐", first.title)
        assertEquals(3, first.participantCount)
        assertEquals(10, first.letterCount)

        val second = result[1]
        assertEquals(2L, second.id)
        assertEquals(5, second.participantCount)
        assertEquals(15, second.letterCount)
    }

    @Test
    fun `인기 캡슐 목록을 가져올 때 요약 정보들이 정확히 반환된다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)

        val capsules = listOf(
            TimeCapsule(
                id = 100L,
                creator = mockk(),
                inviteCode = "POPULAR1",
                title = "인기 캡슐1",
                subtitle = "sub1",
                accessType = AccessType.PUBLIC,
                openAt = now.plusDays(1),
                closedAt = now.minusDays(1)
            ),
            TimeCapsule(
                id = 101L,
                creator = mockk(),
                inviteCode = "POPULAR2",
                title = "인기 캡슐2",
                subtitle = "sub2",
                accessType = AccessType.PUBLIC,
                openAt = now.plusDays(2),
                closedAt = now
            )
        )

        every { capsuleReader.getPopularTimeCapsules(any()) } returns capsules
        every { timeCapsuleUserReader.getParticipantCountMap(listOf(100L, 101L)) } returns mapOf(100L to 7, 101L to 9)
        every { letterReader.getLetterCountMap(listOf(100L, 101L)) } returns mapOf(100L to 20, 101L to 30)

        // when
        val result = detailService.getPopularTimeCapsules(limit = 2)

        // then
        assertEquals(2, result.size)
        assertEquals(100L, result[0].id)
        assertEquals(7, result[0].participantCount)
        assertEquals(20, result[0].letterCount)
        assertEquals(101L, result[1].id)
        assertEquals(9, result[1].participantCount)
        assertEquals(30, result[1].letterCount)
    }
}
