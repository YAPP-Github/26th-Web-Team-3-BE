package com.yapp.lettie.api.letter.service

import com.yapp.lettie.api.file.service.writer.FileWriter
import com.yapp.lettie.api.letter.service.dto.CreateLetterPayload
import com.yapp.lettie.api.letter.service.dto.GetLettersPayload
import com.yapp.lettie.api.letter.service.reader.LetterReader
import com.yapp.lettie.api.letter.service.writer.LetterWriter
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.user.service.reader.UserReader
import com.yapp.lettie.common.dto.UserInfoPayload
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.file.entity.LetterFile
import com.yapp.lettie.domain.letter.entity.Letter
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
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
        val content = "테스트 편지 내용"
        val from = "테스트 발신자"
        val fileKey = "test-file-key"

        val payload =
            CreateLetterPayload(
                capsuleId = capsuleId,
                content = content,
                from = from,
                objectKey = fileKey,
            )

        val user =
            mockk<User> {
                every { id } returns userId
            }

        val timeCapsuleUser = mockk<TimeCapsuleUser>(relaxed = true)
        every { timeCapsuleUser.user } returns user

        val capsule = mockk<TimeCapsule>()
        every { capsule.isClosed(any()) } returns false
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)

        val savedLetter =
            mockk<Letter>(relaxed = true) {
                every { id } returns 100L
            }
        val savedFile = mockk<LetterFile>()

        every { timeCapsuleReader.getById(capsuleId) } returns capsule
        every { userReader.getById(userId) } returns user
        every { letterWriter.save(any()) } returns savedLetter
        every { fileWriter.save(any()) } returns savedFile

        // when
        val result = letterService.writeLetter(userId, payload)

        // then
        assertEquals(100L, result)
        verify(exactly = 1) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 1) { userReader.getById(userId) }
        verify(exactly = 1) { letterWriter.save(any()) }
        verify(exactly = 1) { fileWriter.save(any()) }
        verify(exactly = 1) { savedLetter.addFile(savedFile) }
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

        val user =
            mockk<User> {
                every { id } returns userId
            }

        val timeCapsuleUser = mockk<TimeCapsuleUser>(relaxed = true)
        every { timeCapsuleUser.user } returns user

        val capsule = mockk<TimeCapsule>()
        every { capsule.isClosed(any()) } returns false
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)

        val savedLetter =
            mockk<Letter>(relaxed = true) {
                every { id } returns 100L
            }

        every { timeCapsuleReader.getById(capsuleId) } returns capsule
        every { userReader.getById(userId) } returns user
        every { letterWriter.save(any()) } returns savedLetter

        // when
        val result = letterService.writeLetter(userId, payload)

        // then
        assertEquals(100L, result)
        verify(exactly = 1) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 1) { userReader.getById(userId) }
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
    fun `타임캡슐에 참여하지 않은 사용자인 경우 예외를 발생시킨다`() {
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

        val otherUser =
            mockk<User> {
                every { id } returns 2L // 다른 사용자 ID
            }

        val timeCapsuleUser = mockk<TimeCapsuleUser>(relaxed = true)
        every { timeCapsuleUser.user } returns otherUser // 현재 사용자가 아닌 다른 사용자

        val capsule = mockk<TimeCapsule>()
        every { capsule.isClosed(any()) } returns false
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser) // 해당 사용자가 포함되지 않음

        every { timeCapsuleReader.getById(capsuleId) } returns capsule
        every { userReader.getById(userId) } returns user

        // when & then
        val exception =
            assertThrows(ApiErrorException::class.java) {
                letterService.writeLetter(userId, payload)
            }

        assertEquals(ErrorMessages.NOT_JOINED_TIME_CAPSULE.message, exception.error.message)
        verify(exactly = 1) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 1) { userReader.getById(userId) }
        verify(exactly = 0) { letterWriter.save(any()) }
        verify(exactly = 0) { fileWriter.save(any()) }
    }

    // readLetter 메서드 테스트
    @Test
    fun `편지 조회에 성공한다 - 일반 사용자`() {
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
        every { capsule.isNotOpen(any()) } returns false
        every { capsule.isPrivate() } returns true
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser)

        // Letter mock을 더 정확하게 설정
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
        val result = letterService.readLetter(userPayload, payload)

        // then
        assertEquals(1, result.letters.size)
        assertEquals(20, result.size)
        assertEquals(1, result.totalPages)
        assertEquals(1L, result.totalCount)
        verify(exactly = 1) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 1) { letterReader.findByCapsuleId(capsuleId, pageable) }
    }

    @Test
    fun `편지 조회에 성공한다 - 공개 타임캡슐`() {
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
        every { capsule.isNotOpen(any()) } returns false
        every { capsule.isPrivate() } returns false // 공개 타임캡슐

        val mockUser =
            mockk<User> {
                every { id } returns 2L // 다른 사용자
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
        val result = letterService.readLetter(userPayload, payload)

        // then
        assertEquals(1, result.letters.size)
        assertEquals(20, result.size)
        assertEquals(1, result.totalPages)
        assertEquals(1L, result.totalCount)
        verify(exactly = 1) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 1) { letterReader.findByCapsuleId(capsuleId, pageable) }
    }

    @Test
    fun `타임캡슐이 아직 열리지 않은 경우 예외를 발생시킨다`() {
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
        every { capsule.isNotOpen(any()) } returns true

        every { timeCapsuleReader.getById(capsuleId) } returns capsule

        // when & then
        val exception =
            assertThrows(ApiErrorException::class.java) {
                letterService.readLetter(user, payload)
            }

        assertEquals(ErrorMessages.NOT_OPENED_CAPSULE.message, exception.error.message)
        verify(exactly = 1) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 0) { letterReader.findByCapsuleId(any(), any()) }
    }

    @Test
    fun `편지 조회 시 프라이빗 타임캡슐에 참여하지 않은 사용자인 경우 예외를 발생시킨다`() {
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
                every { id } returns 2L // 다른 사용자 ID
            }

        val timeCapsuleUser = mockk<TimeCapsuleUser>(relaxed = true)
        every { timeCapsuleUser.user } returns otherUser

        val capsule = mockk<TimeCapsule>()
        every { capsule.isNotOpen(any()) } returns false
        every { capsule.isPrivate() } returns true // 프라이빗 타임캡슐
        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser) // 해당 사용자가 포함되지 않음

        every { timeCapsuleReader.getById(capsuleId) } returns capsule

        // when & then
        val exception =
            assertThrows(ApiErrorException::class.java) {
                letterService.readLetter(user, payload)
            }

        assertEquals(ErrorMessages.NOT_JOINED_TIME_CAPSULE.message, exception.error.message)
        verify(exactly = 1) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 0) { letterReader.findByCapsuleId(any(), any()) }
    }
}
