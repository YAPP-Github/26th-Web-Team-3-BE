package com.yapp.lettie.api.letter.service

import com.yapp.lettie.api.file.service.writer.FileWriter
import com.yapp.lettie.api.letter.service.dto.CreateLetterPayload
import com.yapp.lettie.api.letter.service.dto.GetLettersPayload
import com.yapp.lettie.api.letter.service.reader.LetterReader
import com.yapp.lettie.api.letter.service.writer.LetterWriter
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleUserReader
import com.yapp.lettie.api.user.service.reader.UserReader
import com.yapp.lettie.common.dto.UserInfoPayload
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.file.entity.LetterFile
import com.yapp.lettie.domain.letter.entity.Letter
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleUserStatus
import com.yapp.lettie.domain.user.UserRole
import com.yapp.lettie.domain.user.entity.User
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class LetterServiceTest {
    @MockK
    private lateinit var timeCapsuleReader: TimeCapsuleReader

    @MockK
    private lateinit var timeCapsuleUserReader: TimeCapsuleUserReader

    @MockK
    private lateinit var userReader: UserReader

    @MockK
    private lateinit var letterWriter: LetterWriter

    @MockK
    private lateinit var letterReader: LetterReader

    @MockK
    private lateinit var fileWriter: FileWriter

    @InjectMockKs
    private lateinit var letterService: LetterService

    @Test
    fun `편지 작성에 성공한다`() {
        // given
        val userId = 1L
        val capsuleId = 1L
        val payload = CreateLetterPayload(capsuleId, "내용", "보낸이", "file-key")

        val user = mockk<User> { every { id } returns userId }
        val capsule =
            mockk<TimeCapsule> {
                every { isClosed(any()) } returns false
                every { id } returns capsuleId
            }

        val savedLetter = mockk<Letter>(relaxed = true) { every { id } returns 100L }
        val savedFile = mockk<LetterFile>()

        every { timeCapsuleReader.getById(capsuleId) } returns capsule
        every { userReader.getById(userId) } returns user
        every { timeCapsuleUserReader.getTimeCapsuleUserOrNull(capsuleId, userId) } returns null
        every { timeCapsuleUserReader.hasUserJoinedCapsule(userId, capsuleId) } returns true
        every { letterWriter.save(any()) } returns savedLetter
        every { fileWriter.save(any()) } returns savedFile

        // when
        val result = letterService.writeLetter(userId, payload)

        // then
        assertEquals(100L, result)
        verify(exactly = 1) { timeCapsuleUserReader.getTimeCapsuleUserOrNull(capsuleId, userId) }
        verify(exactly = 1) { timeCapsuleUserReader.hasUserJoinedCapsule(userId, capsuleId) }
    }

    @Test
    fun `파일 첨부 없이 편지 작성에 성공한다`() {
        // given
        val userId = 1L
        val capsuleId = 1L
        val content = "테스트 편지 내용"
        val from = "테스트 발신자"

        val payload =
            CreateLetterPayload(
                capsuleId = capsuleId,
                content = content,
                from = from,
                objectKey = null,
            )

        val user = mockk<User> { every { id } returns userId }

        val timeCapsuleUser = mockk<TimeCapsuleUser>(relaxed = true)
        every { timeCapsuleUser.user } returns user

        val capsule = mockk<TimeCapsule>()
        every { capsule.isClosed(any()) } returns false
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)
        every { capsule.id } returns capsuleId

        val savedLetter = mockk<Letter>(relaxed = true) { every { id } returns 100L }

        every { timeCapsuleReader.getById(capsuleId) } returns capsule
        every { userReader.getById(userId) } returns user
        every { timeCapsuleUserReader.getTimeCapsuleUserOrNull(capsuleId, userId) } returns null
        every { timeCapsuleUserReader.hasUserJoinedCapsule(userId, capsuleId) } returns true
        every { letterWriter.save(any()) } returns savedLetter

        // when
        val result = letterService.writeLetter(userId, payload)

        // then
        assertEquals(100L, result)
        verify(exactly = 1) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 1) { userReader.getById(userId) }
        verify(exactly = 1) { timeCapsuleUserReader.getTimeCapsuleUserOrNull(capsuleId, userId) }
        verify(exactly = 1) { timeCapsuleUserReader.hasUserJoinedCapsule(userId, capsuleId) }
        verify(exactly = 1) { letterWriter.save(any()) }
        verify(exactly = 0) { fileWriter.save(any()) }
        verify(exactly = 0) { savedLetter.addFile(any()) }
    }

    @Test
    fun `타임캡슐이 이미 닫힌 경우 예외를 발생시킨다`() {
        // given
        val userId = 1L
        val capsuleId = 1L
        val content = "테스트 편지 내용"
        val from = "테스트 발신자"

        val payload =
            CreateLetterPayload(
                capsuleId = capsuleId,
                content = content,
                from = from,
                objectKey = null,
            )

        val user =
            mockk<User> {
                every { id } returns userId
            }

        val capsule =
            mockk<TimeCapsule> {
                every { isClosed(any()) } returns true
            }

        every { timeCapsuleReader.getById(capsuleId) } returns capsule
        every { userReader.getById(userId) } returns user

        // when & then
        val exception =
            assertThrows(ApiErrorException::class.java) {
                letterService.writeLetter(userId, payload)
            }

        assertEquals(ErrorMessages.CLOSED_TIME_CAPSULE.message, exception.error.message)
        verify(exactly = 1) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 1) { userReader.getById(userId) }
        verify(exactly = 0) { letterWriter.save(any()) }
        verify(exactly = 0) { fileWriter.save(any()) }
    }

    @Test
    fun `나간 사용자가 편지 작성을 시도하면 예외를 발생시킨다`() {
        // given
        val userId = 1L
        val capsuleId = 1L
        val payload =
            CreateLetterPayload(
                capsuleId = capsuleId,
                content = "테스트 편지 내용",
                from = "테스트 발신자",
                objectKey = null,
            )

        val user = mockk<User> { every { id } returns userId }
        val capsule = mockk<TimeCapsule> { every { isClosed(any()) } returns false }

        val leftUser =
            mockk<TimeCapsuleUser> {
                every { status } returns TimeCapsuleUserStatus.LEFT
            }

        every { timeCapsuleReader.getById(capsuleId) } returns capsule
        every { userReader.getById(userId) } returns user
        every { timeCapsuleUserReader.getTimeCapsuleUserOrNull(capsuleId, userId) } returns leftUser

        // when & then
        val exception =
            assertThrows(ApiErrorException::class.java) {
                letterService.writeLetter(userId, payload)
            }

        assertEquals(ErrorMessages.ALREADY_LEFT_CAPSULE.message, exception.error.message)
        verify(exactly = 1) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 1) { userReader.getById(userId) }
        verify(exactly = 1) { timeCapsuleUserReader.getTimeCapsuleUserOrNull(capsuleId, userId) }
        verify(exactly = 0) { timeCapsuleUserReader.hasUserJoinedCapsule(any(), any()) }
        verify(exactly = 0) { letterWriter.save(any()) }
        verify(exactly = 0) { fileWriter.save(any()) }
    }

    // readLetter 메서드 테스트 (단일 편지 조회)
    @Test
    fun `단일 편지 조회에 성공한다`() {
        // given
        val userId = 1L
        val letterId = 1L

        val userPayload =
            UserInfoPayload(
                id = userId,
                roles = listOf(UserRole.USER.name),
            )

        val mockUser =
            mockk<User> {
                every { id } returns userId
            }

        val timeCapsuleUser = mockk<TimeCapsuleUser>(relaxed = true)
        every { timeCapsuleUser.user } returns mockUser

        val capsule = mockk<TimeCapsule>()
        every { capsule.id } returns 1L
        every { capsule.isNotOpen(any()) } returns false
        every { capsule.isPrivate() } returns true
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)

        val mockLetter =
            mockk<Letter> {
                every { id } returns letterId
                every { isMine(userId) } returns true
                every { content } returns "Test letter content"
                every { from } returns "Test sender"
                every { createdAt } returns LocalDateTime.now()
                every { user } returns mockUser
                every { letterFiles } returns mutableListOf()
                every { timeCapsule } returns capsule
            }

        every { letterReader.getById(letterId) } returns mockLetter
        every { timeCapsuleReader.getById(1L) } returns capsule

        // when
        val result = letterService.readLetter(userPayload, letterId)

        // then
        assertEquals(letterId, result.id)
        assertEquals("Test letter content", result.content)
        assertEquals("Test sender", result.from)
        assertEquals(true, result.isMine)
        verify(exactly = 1) { letterReader.getById(letterId) }
        verify(exactly = 1) { timeCapsuleReader.getById(1L) }
    }

    @Test
    fun `단일 편지 조회 시 타임캡슐이 열리지 않은 경우 예외를 발생시킨다`() {
        // given
        val userId = 1L
        val letterId = 1L

        val user =
            UserInfoPayload(
                id = userId,
                roles = listOf(UserRole.USER.name),
            )

        val capsule = mockk<TimeCapsule>()
        every { capsule.id } returns 1L
        every { capsule.isNotOpen(any()) } returns true

        val mockLetter =
            mockk<Letter> {
                every { timeCapsule } returns capsule
            }

        every { letterReader.getById(letterId) } returns mockLetter
        every { timeCapsuleReader.getById(1L) } returns capsule

        // when & then
        val exception =
            assertThrows(ApiErrorException::class.java) {
                letterService.readLetter(user, letterId)
            }

        assertEquals(ErrorMessages.NOT_OPENED_CAPSULE.message, exception.error.message)
        verify(exactly = 1) { letterReader.getById(letterId) }
        verify(exactly = 1) { timeCapsuleReader.getById(1L) }
    }

    @Test
    fun `단일 편지 조회 시 프라이빗 타임캡슐에 참여하지 않은 사용자인 경우 예외를 발생시킨다`() {
        // given
        val userId = 1L
        val letterId = 1L

        val user =
            UserInfoPayload(
                id = userId,
                roles = listOf(UserRole.USER.name),
            )

        val otherUser =
            mockk<User> {
                every { id } returns 2L
            }

        val timeCapsuleUser = mockk<TimeCapsuleUser>(relaxed = true)
        every { timeCapsuleUser.user } returns otherUser

        val capsule = mockk<TimeCapsule>()
        every { capsule.id } returns 1L
        every { capsule.isNotOpen(any()) } returns false
        every { capsule.isPrivate() } returns true
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)

        val mockLetter =
            mockk<Letter> {
                every { timeCapsule } returns capsule
            }

        every { letterReader.getById(letterId) } returns mockLetter
        every { timeCapsuleReader.getById(1L) } returns capsule

        // when & then
        val exception =
            assertThrows(ApiErrorException::class.java) {
                letterService.readLetter(user, letterId)
            }

        assertEquals(ErrorMessages.NOT_JOINED_TIME_CAPSULE.message, exception.error.message)
        verify(exactly = 1) { letterReader.getById(letterId) }
        verify(exactly = 1) { timeCapsuleReader.getById(1L) }
    }

    @Test
    fun `단일 편지 조회 시 공개 타임캡슐은 참여하지 않아도 조회 가능하다`() {
        // given
        val userId = 1L
        val letterId = 1L

        val userPayload =
            UserInfoPayload(
                id = userId,
                roles = listOf(UserRole.USER.name),
            )

        val otherUser =
            mockk<User> {
                every { id } returns 2L
            }

        val capsule = mockk<TimeCapsule>()
        every { capsule.id } returns 1L
        every { capsule.isNotOpen(any()) } returns false
        every { capsule.isPrivate() } returns false // 공개 타임캡슐

        val mockLetter =
            mockk<Letter> {
                every { id } returns letterId
                every { isMine(userId) } returns false
                every { content } returns "Test letter content"
                every { from } returns "Test sender"
                every { createdAt } returns LocalDateTime.now()
                every { user } returns otherUser
                every { letterFiles } returns mutableListOf()
                every { timeCapsule } returns capsule
            }

        every { letterReader.getById(letterId) } returns mockLetter
        every { timeCapsuleReader.getById(1L) } returns capsule

        // when
        val result = letterService.readLetter(userPayload, letterId)

        // then
        assertEquals(letterId, result.id)
        assertEquals("Test letter content", result.content)
        assertEquals("Test sender", result.from)
        assertEquals(false, result.isMine)
        verify(exactly = 1) { letterReader.getById(letterId) }
        verify(exactly = 1) { timeCapsuleReader.getById(1L) }
    }

    // readLetters 메서드 테스트 (목록 조회) - 기존 테스트를 readLetters로 변경
    @Test
    fun `편지 목록 조회에 성공한다 - 일반 사용자`() {
        // given
        val userId = 1L
        val capsuleId = 1L
        val pageable = PageRequest.of(0, 20)

        val userPayload =
            UserInfoPayload(
                id = userId,
                roles = listOf(UserRole.USER.name),
            )

        val payload =
            GetLettersPayload(
                capsuleId = capsuleId,
                pageable = pageable,
            )

        val mockUser =
            mockk<User> {
                every { id } returns userId
            }

        val timeCapsuleUser = mockk<TimeCapsuleUser>(relaxed = true)
        every { timeCapsuleUser.user } returns mockUser

        val capsule = mockk<TimeCapsule>()
        every { capsule.id } returns capsuleId
        every { capsule.isNotOpen(any()) } returns false
        every { capsule.isPrivate() } returns true
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)

        val mockLetter =
            mockk<Letter> {
                every { isMine(userId) } returns true
                every { id } returns 1L
                every { content } returns "Test content"
                every { from } returns "Test sender"
                every { createdAt } returns LocalDateTime.now()
                every { user } returns mockUser
                every { letterFiles } returns mutableListOf()
            }

        val letters = listOf(mockLetter)
        val letterPage: Page<Letter> = PageImpl(letters, pageable, letters.size.toLong())

        every { timeCapsuleReader.getById(capsuleId) } returns capsule
        every { letterReader.findByCapsuleId(capsuleId, pageable) } returns letterPage

        // when
        val result = letterService.readLetters(userPayload, payload)

        // then
        assertEquals(1, result.letters.size)
        assertEquals(20, result.size)
        assertEquals(1, result.totalPages)
        assertEquals(1L, result.totalCount)
        verify(exactly = 2) { timeCapsuleReader.getById(capsuleId) } // validateTimeCapsuleRead에서 한번 더 호출
        verify(exactly = 1) { letterReader.findByCapsuleId(capsuleId, pageable) }
    }

    @Test
    fun `편지 목록 조회에 성공한다 - 공개 타임캡슐`() {
        // given
        val userId = 1L
        val capsuleId = 1L
        val pageable = PageRequest.of(0, 20)

        val userPayload =
            UserInfoPayload(
                id = userId,
                roles = listOf(UserRole.USER.name),
            )

        val payload =
            GetLettersPayload(
                capsuleId = capsuleId,
                pageable = pageable,
            )

        val capsule = mockk<TimeCapsule>()
        every { capsule.id } returns capsuleId
        every { capsule.isNotOpen(any()) } returns false
        every { capsule.isPrivate() } returns false

        val mockUser =
            mockk<User> {
                every { id } returns 2L
            }

        val mockLetter =
            mockk<Letter> {
                every { isMine(userId) } returns false
                every { id } returns 1L
                every { content } returns "Test content"
                every { from } returns "Test sender"
                every { createdAt } returns LocalDateTime.now()
                every { user } returns mockUser
                every { letterFiles } returns mutableListOf()
            }

        val letters = listOf(mockLetter)
        val letterPage: Page<Letter> = PageImpl(letters, pageable, letters.size.toLong())

        every { timeCapsuleReader.getById(capsuleId) } returns capsule
        every { letterReader.findByCapsuleId(capsuleId, pageable) } returns letterPage

        // when
        val result = letterService.readLetters(userPayload, payload)

        // then
        assertEquals(1, result.letters.size)
        assertEquals(20, result.size)
        assertEquals(1, result.totalPages)
        assertEquals(1L, result.totalCount)
        verify(exactly = 2) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 1) { letterReader.findByCapsuleId(capsuleId, pageable) }
    }

    @Test
    fun `편지 목록 조회 시 타임캡슐이 아직 열리지 않은 경우 예외를 발생시킨다`() {
        // given
        val userId = 1L
        val capsuleId = 1L
        val pageable = PageRequest.of(0, 20)

        val user =
            UserInfoPayload(
                id = userId,
                roles = listOf(UserRole.USER.name),
            )

        val payload =
            GetLettersPayload(
                capsuleId = capsuleId,
                pageable = pageable,
            )

        val capsule = mockk<TimeCapsule>()
        every { capsule.id } returns capsuleId
        every { capsule.isNotOpen(any()) } returns true

        every { timeCapsuleReader.getById(capsuleId) } returns capsule

        // when & then
        val exception =
            assertThrows(ApiErrorException::class.java) {
                letterService.readLetters(user, payload)
            }

        assertEquals(ErrorMessages.NOT_OPENED_CAPSULE.message, exception.error.message)
        verify(exactly = 2) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 0) { letterReader.findByCapsuleId(any(), any()) }
    }

    @Test
    fun `편지 목록 조회 시 프라이빗 타임캡슐에 참여하지 않은 사용자인 경우 예외를 발생시킨다`() {
        // given
        val userId = 1L
        val capsuleId = 1L
        val pageable = PageRequest.of(0, 20)

        val user =
            UserInfoPayload(
                id = userId,
                roles = listOf(UserRole.USER.name),
            )

        val payload =
            GetLettersPayload(
                capsuleId = capsuleId,
                pageable = pageable,
            )

        val otherUser =
            mockk<User> {
                every { id } returns 2L
            }

        val timeCapsuleUser = mockk<TimeCapsuleUser>(relaxed = true)
        every { timeCapsuleUser.user } returns otherUser

        val capsule = mockk<TimeCapsule>()
        every { capsule.id } returns capsuleId
        every { capsule.isNotOpen(any()) } returns false
        every { capsule.isPrivate() } returns true
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)

        every { timeCapsuleReader.getById(capsuleId) } returns capsule

        // when & then
        val exception =
            assertThrows(ApiErrorException::class.java) {
                letterService.readLetters(user, payload)
            }

        assertEquals(ErrorMessages.NOT_JOINED_TIME_CAPSULE.message, exception.error.message)
        verify(exactly = 2) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 0) { letterReader.findByCapsuleId(any(), any()) }
    }
}
