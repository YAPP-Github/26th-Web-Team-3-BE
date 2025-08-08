package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.file.service.FileService
import com.yapp.lettie.api.file.service.dto.PresignedUrlDto
import com.yapp.lettie.api.letter.service.reader.LetterReader
import com.yapp.lettie.api.timecapsule.service.dto.SearchTimeCapsulesPayload
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleUserReader
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleLike
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import com.yapp.lettie.domain.timecapsule.entity.vo.CapsuleSort
import com.yapp.lettie.domain.timecapsule.entity.vo.MyCapsuleFilter
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
class TimeCapsuleDetailServiceTest {
    @MockK
    lateinit var fileService: FileService

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
        every {
            fileService.generatePresignedDownloadUrlByObjectKey("CAPSULE/detail_bead0.png")
        } returns
            PresignedUrlDto(
                url = "https://mocked-url.com/CAPSULE/detail_bead0.png",
                key = "CAPSULE/detail_bead0.png",
                expireAt = now.plusMinutes(5),
            )

        // when
        val result = detailService.getTimeCapsuleDetail(capsuleId, userId)

        // then
        assertEquals(capsuleId, result.id)
        assertEquals("캡슐 제목", result.title)
        assertEquals(TimeCapsuleStatus.WRITABLE, result.status)
        assertTrue(result.isLiked)
        assertEquals(1, result.likeCount)
        assertEquals(2, result.participantCount)
        assertNotNull(result.remainingTime)
        assertEquals(1, result.remainingTime?.days)
        assertEquals("https://mocked-url.com/CAPSULE/detail_bead0.png", result.beadVideoUrl)
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
        every {
            fileService.generatePresignedDownloadUrlByObjectKey("CAPSULE/detail_bead0.png")
        } returns
            PresignedUrlDto(
                url = "https://mocked-url.com/CAPSULE/detail_bead0.png",
                key = "CAPSULE/detail_bead0.mp4",
                expireAt = now.plusMinutes(5),
            )

        // when
        val result = detailService.getTimeCapsuleDetail(capsuleId, userId)

        // then
        assertEquals(TimeCapsuleStatus.WAITING_OPEN, result.status)
        assertNotNull(result.remainingTime)
        assertEquals(2, result.remainingTime?.days)
        assertEquals(23, result.remainingTime?.hours)
        assertEquals(59, result.remainingTime?.minutes)
        assertEquals("https://mocked-url.com/CAPSULE/detail_bead0.png", result.beadVideoUrl)
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
        every {
            fileService.generatePresignedDownloadUrlByObjectKey("CAPSULE/detail_bead0.png")
        } returns
            PresignedUrlDto(
                url = "https://mocked-url.com/CAPSULE/detail_bead0.png",
                key = "CAPSULE/detail_bead0.png",
                expireAt = now.plusMinutes(5),
            )

        // when
        val result = detailService.getTimeCapsuleDetail(capsuleId, userId)

        // then
        assertEquals(TimeCapsuleStatus.OPENED, result.status)
        assertEquals(now.minusDays(2).toLocalDate(), result.remainingTime?.openDate)
        assertEquals("https://mocked-url.com/CAPSULE/detail_bead0.png", result.beadVideoUrl)
    }

    @Test
    fun `내 캡슐 목록을 가져올 때 페이지 정보와 요약 정보들이 정확히 반환된다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val userId = 1L
        val capsules =
            listOf(
                TimeCapsule(
                    id = 1L,
                    creator = mockk { every { id } returns userId },
                    inviteCode = "CODE1",
                    title = "내 첫 캡슐",
                    subtitle = "서브1",
                    accessType = AccessType.PUBLIC,
                    openAt = now.plusDays(3),
                    closedAt = now.plusDays(1),
                ),
                TimeCapsule(
                    id = 2L,
                    creator = mockk { every { id } returns userId },
                    inviteCode = "CODE2",
                    title = "내 두 번째 캡슐",
                    subtitle = "서브2",
                    accessType = AccessType.PUBLIC,
                    openAt = now.plusDays(5),
                    closedAt = now.plusDays(2),
                ),
            )

        val pageable = PageRequest.of(0, 2)
        val page = PageImpl(capsules, pageable, capsules.size.toLong())

        every {
            capsuleReader.getMyTimeCapsules(
                userId,
                MyCapsuleFilter.CREATED,
                CapsuleSort.DEFAULT,
                any(),
                any(),
            )
        } returns page
        every { timeCapsuleUserReader.getParticipantCountMap(listOf(1L, 2L)) } returns
            mapOf(1L to 3, 2L to 5)
        every { letterReader.getLetterCountMap(listOf(1L, 2L)) } returns
            mapOf(1L to 10, 2L to 15)

        // when
        val result =
            detailService.getMyTimeCapsules(
                userId,
                MyCapsuleFilter.CREATED,
                CapsuleSort.DEFAULT,
                pageable,
            )

        // then
        assertEquals(2, result.timeCapsules.size)
        assertEquals(2L, result.totalCount)

        with(result.timeCapsules[0]) {
            assertEquals(1L, id)
            assertEquals("내 첫 캡슐", title)
            assertEquals(3, participantCount)
            assertEquals(10, letterCount)
        }
        with(result.timeCapsules[1]) {
            assertEquals(2L, id)
            assertEquals("내 두 번째 캡슐", title)
            assertEquals(5, participantCount)
            assertEquals(15, letterCount)
        }
    }

    @Test
    fun `내 캡슐 목록이 비어있을 때 빈 페이지가 반환된다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val userId = 1L
        val pageable = PageRequest.of(0, 2)
        val emptyPage = PageImpl<TimeCapsule>(emptyList(), pageable, 0)

        every {
            capsuleReader.getMyTimeCapsules(
                userId,
                MyCapsuleFilter.CREATED,
                CapsuleSort.DEFAULT,
                any(),
                any(),
            )
        } returns emptyPage
        every { timeCapsuleUserReader.getParticipantCountMap(emptyList()) } returns emptyMap()
        every { letterReader.getLetterCountMap(emptyList()) } returns emptyMap()

        // when
        val result =
            detailService.getMyTimeCapsules(
                userId,
                MyCapsuleFilter.CREATED,
                CapsuleSort.DEFAULT,
                pageable,
            )

        // then
        assertTrue(result.timeCapsules.isEmpty())
        assertEquals(0L, result.totalCount)
    }

    @Test
    fun `인기 캡슐 목록을 가져올 때 페이지 정보와 요약 정보들이 정확히 반환된다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val capsules =
            listOf(
                TimeCapsule(
                    id = 100L,
                    creator = mockk(),
                    inviteCode = "POPULAR1",
                    title = "인기 캡슐1",
                    subtitle = "sub1",
                    accessType = AccessType.PUBLIC,
                    openAt = now.plusDays(1),
                    closedAt = now.minusDays(1),
                ),
                TimeCapsule(
                    id = 101L,
                    creator = mockk(),
                    inviteCode = "POPULAR2",
                    title = "인기 캡슐2",
                    subtitle = "sub2",
                    accessType = AccessType.PUBLIC,
                    openAt = now.plusDays(2),
                    closedAt = now,
                ),
            )
        val pageable = PageRequest.of(0, 2)
        val page = PageImpl(capsules, pageable, 2)
        every { capsuleReader.getPopularTimeCapsules(any()) } returns page
        every { timeCapsuleUserReader.getParticipantCountMap(listOf(100L, 101L)) } returns mapOf(100L to 7, 101L to 9)
        every { letterReader.getLetterCountMap(listOf(100L, 101L)) } returns mapOf(100L to 20, 101L to 30)

        // when
        val result = detailService.getPopularTimeCapsules(pageable)

        // then
        assertEquals(2, result.timeCapsules.size)
        assertEquals(2L, result.totalCount)
        assertEquals(100L, result.timeCapsules[0].id)
        assertEquals(7, result.timeCapsules[0].participantCount)
        assertEquals(20, result.timeCapsules[0].letterCount)
        assertEquals(101L, result.timeCapsules[1].id)
        assertEquals(9, result.timeCapsules[1].participantCount)
        assertEquals(30, result.timeCapsules[1].letterCount)
    }

    @Test
    fun `인기 캡슐 목록이 비어있을 때 빈 페이지가 반환된다`() {
        // given
        val pageable = PageRequest.of(0, 2)
        val page = PageImpl(listOf<TimeCapsule>(), pageable, 0)
        every { capsuleReader.getPopularTimeCapsules(any()) } returns page
        every { timeCapsuleUserReader.getParticipantCountMap(emptyList()) } returns emptyMap()
        every { letterReader.getLetterCountMap(emptyList()) } returns emptyMap()

        // when
        val result = detailService.getPopularTimeCapsules(pageable)

        // then
        assertTrue(result.timeCapsules.isEmpty())
        assertEquals(0L, result.totalCount)
    }

    @Test
    fun `캡슐을 키워드로 검색할 때 PUBLIC 타입만 검색되고 페이지 정보가 정확히 반환된다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val keyword = "테스트"
        val capsules =
            listOf(
                TimeCapsule(
                    id = 1L,
                    creator = mockk(),
                    inviteCode = "SEARCH1",
                    title = "테스트 캡슐 1",
                    subtitle = "검색 테스트용",
                    accessType = AccessType.PUBLIC,
                    openAt = now.plusDays(1),
                    closedAt = now.minusDays(1),
                ),
                TimeCapsule(
                    id = 2L,
                    creator = mockk(),
                    inviteCode = "SEARCH2",
                    title = "테스트 캡슐 2",
                    subtitle = "또 다른 검색 테스트",
                    accessType = AccessType.PUBLIC,
                    openAt = now.plusDays(2),
                    closedAt = now.minusDays(2),
                ),
            )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(capsules, pageable, 2)
        every { capsuleReader.searchTimeCapsules(keyword, any()) } returns page
        every { timeCapsuleUserReader.getParticipantCountMap(listOf(1L, 2L)) } returns mapOf(1L to 5, 2L to 8)
        every { letterReader.getLetterCountMap(listOf(1L, 2L)) } returns mapOf(1L to 12, 2L to 25)

        // when
        val payload = SearchTimeCapsulesPayload(keyword = keyword, pageable = pageable)
        val result = detailService.searchTimeCapsules(payload)

        // then
        assertEquals(2, result.timeCapsules.size)
        assertEquals(2L, result.totalCount)
        assertEquals(0, result.page)
        assertEquals(1, result.totalPages)

        val first = result.timeCapsules[0]
        assertEquals(1L, first.id)
        assertEquals("테스트 캡슐 1", first.title)
        assertEquals(5, first.participantCount)
        assertEquals(12, first.letterCount)

        val second = result.timeCapsules[1]
        assertEquals(2L, second.id)
        assertEquals("테스트 캡슐 2", second.title)
        assertEquals(8, second.participantCount)
        assertEquals(25, second.letterCount)
    }

    @Test
    fun `키워드로 검색했을 때 결과가 없으면 빈 페이지가 반환된다`() {
        // given
        val keyword = "존재하지않는키워드"
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(listOf<TimeCapsule>(), pageable, 0)
        every { capsuleReader.searchTimeCapsules(keyword, any()) } returns page
        every { timeCapsuleUserReader.getParticipantCountMap(emptyList()) } returns emptyMap()
        every { letterReader.getLetterCountMap(emptyList()) } returns emptyMap()

        // when
        val payload = SearchTimeCapsulesPayload(keyword = keyword, pageable = pageable)
        val result = detailService.searchTimeCapsules(payload)

        // then
        assertTrue(result.timeCapsules.isEmpty())
        assertEquals(0L, result.totalCount)
        assertEquals(0, result.page)
        assertEquals(0, result.totalPages)
    }

    @Test
    fun `키워드로 검색할 때 페이지네이션이 올바르게 동작한다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val keyword = "캡슐"
        val capsules =
            listOf(
                TimeCapsule(
                    id = 10L,
                    creator = mockk(),
                    inviteCode = "PAGE1",
                    title = "페이지 캡슐 1",
                    subtitle = "첫 번째 페이지",
                    accessType = AccessType.PUBLIC,
                    openAt = now.plusDays(1),
                    closedAt = now.minusDays(1),
                ),
            )
        val pageable = PageRequest.of(1, 5) // 두 번째 페이지, 5개씩
        val page = PageImpl(capsules, pageable, 15) // 전체 15개 중 두 번째 페이지
        every { capsuleReader.searchTimeCapsules(keyword, any()) } returns page
        every { timeCapsuleUserReader.getParticipantCountMap(listOf(10L)) } returns mapOf(10L to 3)
        every { letterReader.getLetterCountMap(listOf(10L)) } returns mapOf(10L to 7)

        // when
        val payload = SearchTimeCapsulesPayload(keyword = keyword, pageable = pageable)
        val result = detailService.searchTimeCapsules(payload)

        // then
        assertEquals(1, result.timeCapsules.size) // 현재 페이지의 컨텐츠
        assertEquals(15L, result.totalCount) // 전체 개수
        assertEquals(1, result.page) // 현재 페이지 (0-based)
        assertEquals(3, result.totalPages) // 전체 페이지 수 (15/5 = 3)
    }

    @Test
    fun `빈 키워드로 검색할 때도 정상적으로 처리된다`() {
        // given
        val keyword = ""
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(listOf<TimeCapsule>(), pageable, 0)
        every { capsuleReader.searchTimeCapsules(keyword, any()) } returns page
        every { timeCapsuleUserReader.getParticipantCountMap(emptyList()) } returns emptyMap()
        every { letterReader.getLetterCountMap(emptyList()) } returns emptyMap()

        // when
        val payload = SearchTimeCapsulesPayload(keyword = keyword, pageable = pageable)
        val result = detailService.searchTimeCapsules(payload)

        // then
        assertTrue(result.timeCapsules.isEmpty())
        assertEquals(0L, result.totalCount)
    }
}
