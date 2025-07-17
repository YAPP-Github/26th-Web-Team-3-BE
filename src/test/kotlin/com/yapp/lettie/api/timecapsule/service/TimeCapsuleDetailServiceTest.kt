package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.user.service.reader.UserReader
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleLike
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import com.yapp.lettie.domain.user.entity.User
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class TimeCapsuleDetailServiceTest {
    @MockK lateinit var userReader: UserReader

    @MockK lateinit var capsuleReader: TimeCapsuleReader

    @MockK lateinit var likeReader: TimeCapsuleLikeReader

    @InjectMockKs
    lateinit var detailService: TimeCapsuleDetailService

    @Test
    fun `캡슐이 WRITABLE 상태일 때, 상세 정보를 정확히 반환한다`() {
        // given
        val now = LocalDateTime.now()
        val userId = 1L
        val capsuleId = 10L
        val user = mockk<User>()
        val capsule = mockk<TimeCapsule>()
        val like = mockk<TimeCapsuleLike>()
        val userList = listOf(mockk<TimeCapsuleUser>(), mockk())
        val likeList = listOf(mockk<TimeCapsuleLike>().apply { every { isLiked } returns true })

        every { capsuleReader.getById(capsuleId) } returns capsule
        every { userReader.getById(userId) } returns user
        every { likeReader.findByUserAndCapsule(user, capsule) } returns like
        every { like.isLiked } returns true

        every { capsule.id } returns capsuleId
        every { capsule.title } returns "캡슐 제목"
        every { capsule.subtitle } returns "부제목"
        every { capsule.openAt } returns now.plusDays(3)
        every { capsule.closedAt } returns now.plusDays(2)
        every { capsule.timeCapsuleUsers } returns userList.toMutableList()
        every { capsule.timeCapsuleLikes } returns likeList.toMutableList()

        // when
        val result = detailService.getTimeCapsuleDetail(capsuleId, userId)

        // then
        assertEquals(capsuleId, result.id)
        assertEquals("캡슐 제목", result.title)
        assertEquals(TimeCapsuleStatus.WRITABLE, result.status)
        assertEquals(true, result.isLiked)
        assertEquals(1, result.likeCount)
        assertEquals(2, result.participantCount)
        assertNotNull(result.remainingTime)
        assertEquals(1, result.remainingTime?.days)
    }

    @Test
    fun `캡슐이 WAITING_OPEN 상태일 때, 상세 정보를 정확히 반환한다`() {
        // given
        val now = LocalDateTime.now()
        val userId = 2L
        val capsuleId = 20L
        val user = mockk<User>()
        val capsule = mockk<TimeCapsule>()
        val like = mockk<TimeCapsuleLike>()
        val users = listOf(mockk<TimeCapsuleUser>())
        val likes = listOf(mockk<TimeCapsuleLike>().apply { every { isLiked } returns true })

        every { capsuleReader.getById(capsuleId) } returns capsule
        every { userReader.getById(userId) } returns user
        every { likeReader.findByUserAndCapsule(user, capsule) } returns like
        every { like.isLiked } returns true

        every { capsule.id } returns capsuleId
        every { capsule.title } returns "대기 중 캡슐"
        every { capsule.subtitle } returns "기다림의 미학"
        every { capsule.closedAt } returns now.minusDays(2)
        every { capsule.openAt } returns now.plusDays(3)
        every { capsule.timeCapsuleUsers } returns users.toMutableList()
        every { capsule.timeCapsuleLikes } returns likes.toMutableList()

        // when
        val result = detailService.getTimeCapsuleDetail(capsuleId, userId)

        // then
        assertEquals(TimeCapsuleStatus.WAITING_OPEN, result.status)
        assertEquals(2, result.remainingTime?.days)
        assertEquals(23, result.remainingTime?.hours)
        assertEquals(59, result.remainingTime?.minutes)
    }

    @Test
    fun `캡슐이 OPENED 상태일 때, 상세 정보를 정확히 반환한다`() {
        // given
        val now = LocalDateTime.now()
        val capsuleId = 30L
        val capsule = mockk<TimeCapsule>()
        val users = listOf(mockk<TimeCapsuleUser>())
        val likes = listOf(mockk<TimeCapsuleLike>().apply { every { isLiked } returns false })

        every { capsuleReader.getById(capsuleId) } returns capsule
        every { capsule.id } returns capsuleId
        every { capsule.title } returns "오픈된 캡슐"
        every { capsule.subtitle } returns "이제 읽어보세요"
        every { capsule.closedAt } returns now.minusDays(3)
        every { capsule.openAt } returns now.minusDays(2)
        every { capsule.timeCapsuleUsers } returns users.toMutableList()
        every { capsule.timeCapsuleLikes } returns likes.toMutableList()

        // when
        val result = detailService.getTimeCapsuleDetail(capsuleId, null) // 비로그인 사용자

        // then
        assertEquals(TimeCapsuleStatus.OPENED, result.status)
        assertEquals(null, result.isLiked)
        assertEquals(now.minusDays(2).toLocalDate(), result.remainingTime?.openDate)
    }
}
