package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.file.service.FileService
import com.yapp.lettie.api.letter.service.reader.LetterReader
import com.yapp.lettie.api.timecapsule.service.dto.ExploreMyTimeCapsulesPayload
import com.yapp.lettie.api.timecapsule.service.dto.ExploreTimeCapsulesPayload
import com.yapp.lettie.api.timecapsule.service.dto.RemainingTimeDto
import com.yapp.lettie.api.timecapsule.service.dto.SearchTimeCapsulesPayload
import com.yapp.lettie.api.timecapsule.service.dto.TimeCapsuleDetailDto
import com.yapp.lettie.api.timecapsule.service.dto.TimeCapsuleSummariesDto
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleUserReader
import com.yapp.lettie.api.timecapsule.service.writer.TimeCapsuleUserWriter
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TimeCapsuleDetailService(
    private val fileService: FileService,
    private val timeCapsuleReader: TimeCapsuleReader,
    private val timeCapsuleLikeReader: TimeCapsuleLikeReader,
    private val timeCapsuleUserReader: TimeCapsuleUserReader,
    private val timeCapsuleUserWriter: TimeCapsuleUserWriter,
    private val letterReader: LetterReader,
) {
    fun getTimeCapsuleDetail(
        capsuleId: Long,
        userId: Long?,
    ): TimeCapsuleDetailDto {
        val capsule = timeCapsuleReader.getById(capsuleId)
        val now = LocalDateTime.now()

        val liked =
            userId?.let {
                timeCapsuleLikeReader.findByUserIdAndCapsuleId(it, capsuleId)?.isLiked
            } ?: false

        val status = capsule.getStatus(now)
        val remainingTime = RemainingTimeDto.fromStatus(status, now, capsule.openAt, capsule.closedAt)
        val likeCount = timeCapsuleLikeReader.getLikeCount(capsuleId)
        val participantCount = timeCapsuleUserReader.getParticipantCount(capsuleId)

        val (isFirstOpen, isMine, isJoined) =
            if (userId != null) {
                val timeCapsuleUser = timeCapsuleUserReader.getTimeCapsuleUserOrNull(capsuleId, userId)
                val firstOpen = timeCapsuleUser?.let { !it.isOpened } ?: false
                if (firstOpen && timeCapsuleUser != null) {
                    timeCapsuleUser.updateOpened()
                    timeCapsuleUserWriter.save(timeCapsuleUser)
                }
                val joined = timeCapsuleUser?.isActive ?: false
                Triple(firstOpen, capsule.creator.id == userId, joined)
            } else {
                Triple(false, false, false)
            }

        val letterCount = letterReader.getLetterCountByCapsuleId(capsule.id)
        val objectKey = getBeadObjectKey(letterCount)
        val beadVideoUrl = fileService.generatePresignedDownloadUrlByObjectKey(objectKey).url

        return TimeCapsuleDetailDto(
            id = capsule.id,
            title = capsule.title,
            subtitle = capsule.subtitle,
            openAt = capsule.openAt,
            closedAt = capsule.closedAt,
            participantCount = participantCount,
            letterCount = letterCount,
            likeCount = likeCount,
            isLiked = liked,
            status = status,
            remainingTime = remainingTime,
            isMine = isMine,
            isJoined = isJoined,
            inviteCode = capsule.inviteCode,
            beadVideoUrl = beadVideoUrl,
            isFirstOpen = isFirstOpen,
            accessType = capsule.accessType,
        )
    }

    fun getMyTimeCapsules(
        userId: Long,
        payload: ExploreMyTimeCapsulesPayload,
    ): TimeCapsuleSummariesDto {
        val now = LocalDateTime.now()
        val capsules = timeCapsuleReader.getMyTimeCapsules(userId, payload.filter, payload.sort, now, payload.pageable)
        return getTimeCapsuleSummaries(capsules, LocalDateTime.now())
    }

    fun getPopularTimeCapsules(pageable: Pageable): TimeCapsuleSummariesDto {
        val capsules = timeCapsuleReader.getPopularTimeCapsules(pageable)
        return getTimeCapsuleSummaries(capsules, LocalDateTime.now())
    }

    fun exploreTimeCapsules(payload: ExploreTimeCapsulesPayload): TimeCapsuleSummariesDto {
        val now = LocalDateTime.now()
        val capsules = timeCapsuleReader.exploreTimeCapsules(payload.type, payload.sort, now, payload.pageable)
        return getTimeCapsuleSummaries(capsules, now)
    }

    fun searchTimeCapsules(payload: SearchTimeCapsulesPayload): TimeCapsuleSummariesDto {
        val capsules = timeCapsuleReader.searchTimeCapsules(payload.keyword, payload.pageable)
        return getTimeCapsuleSummaries(capsules, LocalDateTime.now())
    }

    private fun getTimeCapsuleSummaries(
        capsules: Page<TimeCapsule>,
        now: LocalDateTime,
    ): TimeCapsuleSummariesDto {
        val capsuleIds = capsules.content.map { it.id }
        val participantCountMap = timeCapsuleUserReader.getParticipantCountMap(capsuleIds)
        val letterCountMap = letterReader.getLetterCountMap(capsuleIds)

        return TimeCapsuleSummariesDto.of(
            capsules = capsules,
            participantCountMap = participantCountMap,
            letterCountMap = letterCountMap,
            now = now,
        )
    }

    private fun getBeadObjectKey(letterCount: Int): String {
        val beadIndex = if (letterCount >= MAX_BEAD_THRESHOLD) MAX_BEAD_INDEX else letterCount / LETTERS_PER_BEAD
        return "CAPSULE/detail_bead$beadIndex.png"
    }

    companion object {
        private const val LETTERS_PER_BEAD = 10
        private const val MAX_BEAD_INDEX = 10
        private const val MAX_BEAD_THRESHOLD = LETTERS_PER_BEAD * MAX_BEAD_INDEX
    }
}
