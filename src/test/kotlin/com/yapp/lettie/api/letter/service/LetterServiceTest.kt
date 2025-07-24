package com.yapp.lettie.api.letter.service

import com.yapp.lettie.api.file.service.writer.FileWriter
import com.yapp.lettie.api.letter.service.writer.LetterWriter
import com.yapp.lettie.api.timecapsule.service.dto.CreateLetterPayload
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.user.service.reader.UserReader
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.file.entity.LetterFile
import com.yapp.lettie.domain.letter.entity.Letter
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
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

@ExtendWith(MockKExtension::class)
class LetterServiceTest {
    @MockK
    private lateinit var timeCapsuleReader: TimeCapsuleReader

    @MockK
    private lateinit var userReader: UserReader

    @MockK
    private lateinit var letterWriter: LetterWriter

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

        val savedLetter = mockk<Letter>(relaxed = true)
        val savedFile = mockk<LetterFile>()

        every { timeCapsuleReader.getById(capsuleId) } returns capsule
        every { userReader.getById(userId) } returns user
        every { letterWriter.save(any()) } returns savedLetter
        every { fileWriter.save(any()) } returns savedFile

        // when
        letterService.writeLetter(userId, payload)

        // then
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

        val savedLetter = mockk<Letter>(relaxed = true)

        every { timeCapsuleReader.getById(capsuleId) } returns capsule
        every { userReader.getById(userId) } returns user
        every { letterWriter.save(any()) } returns savedLetter

        // when
        letterService.writeLetter(userId, payload)

        // then
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

    @Test
    fun `타임캡슐에 참여한 사용자가 여러 명일 때 편지 작성에 성공한다`() {
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

        val otherUser1 =
            mockk<User> {
                every { id } returns 2L
            }

        val otherUser2 =
            mockk<User> {
                every { id } returns 3L
            }

        val timeCapsuleUser1 = mockk<TimeCapsuleUser>(relaxed = true)
        every { timeCapsuleUser1.user } returns user

        val timeCapsuleUser2 =
            mockk<TimeCapsuleUser>(relaxed = true) {
            }
        every { timeCapsuleUser2.user } returns otherUser1

        val timeCapsuleUser3 =
            mockk<TimeCapsuleUser>(relaxed = true) {
            }
        every { timeCapsuleUser3.user } returns otherUser2

        val capsule =
            mockk<TimeCapsule> {
                every { isClosed(any()) } returns false
            }

        every { capsule.timeCapsuleUsers } returns mutableListOf(timeCapsuleUser1, timeCapsuleUser2, timeCapsuleUser3)

        val savedLetter = mockk<Letter>(relaxed = true)

        every { timeCapsuleReader.getById(capsuleId) } returns capsule
        every { userReader.getById(userId) } returns user
        every { letterWriter.save(any()) } returns savedLetter

        // when
        letterService.writeLetter(userId, payload)

        // then
        verify(exactly = 1) { timeCapsuleReader.getById(capsuleId) }
        verify(exactly = 1) { userReader.getById(userId) }
        verify(exactly = 1) { letterWriter.save(any()) }
        verify(exactly = 0) { fileWriter.save(any()) }
    }
}
