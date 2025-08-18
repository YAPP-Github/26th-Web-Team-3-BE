package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.file.service.FileService
import com.yapp.lettie.api.file.service.dto.PresignedUrlDto
import com.yapp.lettie.api.letter.service.reader.LetterReader
import com.yapp.lettie.api.timecapsule.service.dto.ExploreMyTimeCapsulesPayload
import com.yapp.lettie.api.timecapsule.service.dto.SearchTimeCapsulesPayload
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleUserReader
import com.yapp.lettie.api.timecapsule.service.writer.TimeCapsuleUserWriter
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
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.lang.reflect.Method
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
    lateinit var timeCapsuleUserWriter: TimeCapsuleUserWriter

    @MockK
    lateinit var letterReader: LetterReader

    @InjectMockKs
    lateinit var detailService: TimeCapsuleDetailService

    private val getBeadObjectKeyMethod: Method by lazy {
        TimeCapsuleDetailService::class.java
            .getDeclaredMethod("getBeadObjectKey", Int::class.java)
            .apply { isAccessible = true }
    }

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

        val timeCapsuleUser =
            mockk<TimeCapsuleUser> {
                every { isOpened } returns false
                every { isActive } returns true
                every { updateOpened() } returns Unit
            }

        every { capsuleReader.getById(capsuleId) } returns capsule
        every { likeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns
            mockk {
                every { isLiked } returns true
            }
        every { likeReader.getLikeCount(capsuleId) } returns 1
        every { timeCapsuleUserReader.getParticipantCount(capsuleId) } returns 2
        every { timeCapsuleUserReader.findTimeCapsuleUser(capsuleId, userId) } returns timeCapsuleUser
        every { letterReader.getLetterCountByCapsuleId(capsuleId) } returns 3
        every { timeCapsuleUserWriter.save(timeCapsuleUser) } returns Unit
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
        assertEquals(3, result.letterCount)
        assertTrue(result.isFirstOpen) // 첫 방문
        assertNotNull(result.remainingTime)
        assertEquals(1, result.remainingTime?.days)
        assertEquals("https://mocked-url.com/CAPSULE/detail_bead0.png", result.beadVideoUrl)

        // 첫 방문이므로 updateOpened()와 save()가 호출되었는지 확인
        verify { timeCapsuleUser.updateOpened() }
        verify { timeCapsuleUserWriter.save(timeCapsuleUser) }
    }

    @Test
    fun `이미 방문한 캡슐일 때는 isOpened 업데이트를 하지 않는다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val capsuleId = 1L
        val userId = 10L

        val user = mockk<User> { every { id } returns userId }
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
            )

        val timeCapsuleUser =
            mockk<TimeCapsuleUser> {
                every { isOpened } returns true // 이미 방문함
                every { isActive } returns true
            }

        every { capsuleReader.getById(capsuleId) } returns capsule
        every { likeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns null
        every { likeReader.getLikeCount(capsuleId) } returns 0
        every { timeCapsuleUserReader.getParticipantCount(capsuleId) } returns 1
        every { timeCapsuleUserReader.findTimeCapsuleUser(capsuleId, userId) } returns timeCapsuleUser
        every { letterReader.getLetterCountByCapsuleId(capsuleId) } returns 5
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
        assertFalse(result.isFirstOpen) // 이미 방문한 상태

        // updateOpened()와 save()가 호출되지 않았는지 확인
        verify(exactly = 0) { timeCapsuleUser.updateOpened() }
        verify(exactly = 0) { timeCapsuleUserWriter.save(any()) }
    }

    @Test
    fun `getBeadObjectKey 메서드 - 편지 개수가 0개일 때 bead0을 반환한다`() {
        val result = getBeadObjectKeyMethod.invoke(detailService, 0) as String
        assertEquals("CAPSULE/detail_bead0.png", result)
    }

    @Test
    fun `getBeadObjectKey 메서드 - 편지 개수가 1-9개일 때 bead0을 반환한다`() {
        val result1 = getBeadObjectKeyMethod.invoke(detailService, 1) as String
        val result9 = getBeadObjectKeyMethod.invoke(detailService, 9) as String

        assertEquals("CAPSULE/detail_bead0.png", result1)
        assertEquals("CAPSULE/detail_bead0.png", result9)
    }

    @Test
    fun `getBeadObjectKey 메서드 - 편지 개수가 10-19개일 때 bead1을 반환한다`() {
        val result10 = getBeadObjectKeyMethod.invoke(detailService, 10) as String
        val result19 = getBeadObjectKeyMethod.invoke(detailService, 19) as String

        assertEquals("CAPSULE/detail_bead1.png", result10)
        assertEquals("CAPSULE/detail_bead1.png", result19)
    }

    @Test
    fun `getBeadObjectKey 메서드 - 편지 개수가 50개일 때 bead5를 반환한다`() {
        val result = getBeadObjectKeyMethod.invoke(detailService, 50) as String
        assertEquals("CAPSULE/detail_bead5.png", result)
    }

    @Test
    fun `getBeadObjectKey 메서드 - 편지 개수가 100개 이상일 때 최대값인 bead10을 반환한다`() {
        val result100 = getBeadObjectKeyMethod.invoke(detailService, 100) as String
        val result150 = getBeadObjectKeyMethod.invoke(detailService, 150) as String
        val result1000 = getBeadObjectKeyMethod.invoke(detailService, 1000) as String

        assertEquals("CAPSULE/detail_bead10.png", result100)
        assertEquals("CAPSULE/detail_bead10.png", result150)
        assertEquals("CAPSULE/detail_bead10.png", result1000)
    }

    @Test
    fun `getBeadObjectKey 메서드 - 경계값 테스트 99개와 100개`() {
        val result99 = getBeadObjectKeyMethod.invoke(detailService, 99) as String
        val result100 = getBeadObjectKeyMethod.invoke(detailService, 100) as String

        assertEquals("CAPSULE/detail_bead9.png", result99)
        assertEquals("CAPSULE/detail_bead10.png", result100)
    }

    @Test
    fun `편지 개수에 따른 올바른 beadVideoUrl이 반환된다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val capsuleId = 1L
        val userId = 10L
        val letterCount = 25 // bead2가 선택되어야 함

        val user = mockk<User> { every { id } returns userId }
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
            )

        val timeCapsuleUser =
            mockk<TimeCapsuleUser> {
                every { isOpened } returns true
                every { isActive } returns true
            }

        every { capsuleReader.getById(capsuleId) } returns capsule
        every { likeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns null
        every { likeReader.getLikeCount(capsuleId) } returns 0
        every { timeCapsuleUserReader.getParticipantCount(capsuleId) } returns 1
        every { timeCapsuleUserReader.findTimeCapsuleUser(capsuleId, userId) } returns timeCapsuleUser
        every { letterReader.getLetterCountByCapsuleId(capsuleId) } returns letterCount
        every {
            fileService.generatePresignedDownloadUrlByObjectKey("CAPSULE/detail_bead2.png")
        } returns
            PresignedUrlDto(
                url = "https://mocked-url.com/CAPSULE/detail_bead2.png",
                key = "CAPSULE/detail_bead2.png",
                expireAt = now.plusMinutes(5),
            )

        // when
        val result = detailService.getTimeCapsuleDetail(capsuleId, userId)

        // then
        assertEquals("https://mocked-url.com/CAPSULE/detail_bead2.png", result.beadVideoUrl)
        assertEquals(letterCount, result.letterCount)
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

        val timeCapsuleUser =
            mockk<TimeCapsuleUser> {
                every { isOpened } returns true
                every { isActive } returns true
            }

        every { capsuleReader.getById(capsuleId) } returns capsule
        every { likeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns
            mockk {
                every { isLiked } returns true
            }
        every { likeReader.getLikeCount(capsuleId) } returns 1
        every { timeCapsuleUserReader.getParticipantCount(capsuleId) } returns 2
        every { timeCapsuleUserReader.findTimeCapsuleUser(capsuleId, userId) } returns timeCapsuleUser
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
        assertEquals(TimeCapsuleStatus.WAITING_OPEN, result.status)
        assertNotNull(result.remainingTime)
        assertEquals(2, result.remainingTime?.days)
        assertEquals(23, result.remainingTime?.hours)
        assertEquals(59, result.remainingTime?.minutes)
        assertEquals("https://mocked-url.com/CAPSULE/detail_bead0.png", result.beadVideoUrl)
        assertFalse(result.isFirstOpen) // 이미 방문한 상태
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

        val timeCapsuleUser =
            mockk<TimeCapsuleUser> {
                every { isOpened } returns true
                every { isActive } returns true
            }

        every { capsuleReader.getById(capsuleId) } returns capsule
        every { likeReader.findByUserIdAndCapsuleId(userId, capsuleId) } returns
            mockk {
                every { isLiked } returns true
            }
        every { likeReader.getLikeCount(capsuleId) } returns 1
        every { timeCapsuleUserReader.getParticipantCount(capsuleId) } returns 2
        every { timeCapsuleUserReader.findTimeCapsuleUser(capsuleId, userId) } returns timeCapsuleUser
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
        assertFalse(result.isFirstOpen) // 이미 방문한 상태
    }

    @Test
    fun `비로그인 사용자는 liked, isMine, isFirstOpen이 false로 내려가고 업데이트 호출이 없다`() {
        // given
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val capsuleId = 999L
        val creator = mockk<User> { every { id } returns 42L }
        val capsule =
            TimeCapsule(
                id = capsuleId,
                creator = creator,
                inviteCode = "GUEST1",
                title = "게스트도 볼 수 있는 캡슐",
                subtitle = "공개 캡슐",
                accessType = AccessType.PUBLIC,
                openAt = now.plusDays(2),
                closedAt = now.plusDays(1),
            )

        every { capsuleReader.getById(capsuleId) } returns capsule
        every { likeReader.findByUserIdAndCapsuleId(any(), any()) } returns null
        every { likeReader.getLikeCount(capsuleId) } returns 0
        every { timeCapsuleUserReader.getParticipantCount(capsuleId) } returns 0
        every { timeCapsuleUserReader.findTimeCapsuleUser(any(), any()) } returns null
        every { letterReader.getLetterCountByCapsuleId(capsuleId) } returns 0
        every {
            fileService.generatePresignedDownloadUrlByObjectKey("CAPSULE/detail_bead0.png")
        } returns
            PresignedUrlDto(
                url = "https://mocked-url.com/CAPSULE/detail_bead0.png",
                key = "CAPSULE/detail_bead0.png",
                expireAt = now.plusMinutes(5),
            )

        // when
        val result = detailService.getTimeCapsuleDetail(capsuleId, null)

        // then
        assertFalse(result.isLiked)
        assertFalse(result.isMine)
        assertFalse(result.isFirstOpen)
        assertEquals(0, result.likeCount)
        assertEquals(0, result.participantCount)
        assertEquals(0, result.letterCount)
        assertEquals("https://mocked-url.com/CAPSULE/detail_bead0.png", result.beadVideoUrl)

        verify(exactly = 0) { timeCapsuleUserWriter.save(any()) }
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
        val payload =
            ExploreMyTimeCapsulesPayload.of(
                MyCapsuleFilter.CREATED,
                CapsuleSort.DEFAULT,
                pageable,
            )
        val result = detailService.getMyTimeCapsules(userId, payload)

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
        val payload =
            ExploreMyTimeCapsulesPayload.of(
                MyCapsuleFilter.CREATED,
                CapsuleSort.DEFAULT,
                pageable,
            )
        val result = detailService.getMyTimeCapsules(userId, payload)

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
