package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.file.service.FileService
import com.yapp.lettie.api.letter.service.reader.LetterReader
import com.yapp.lettie.api.timecapsule.service.dto.RemainingStatusDto
import com.yapp.lettie.api.timecapsule.service.dto.RemainingTimeDto
import com.yapp.lettie.api.timecapsule.service.dto.TimeCapsuleDetailDto
import com.yapp.lettie.api.timecapsule.service.dto.TimeCapsuleSummaryDto
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleUserReader
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TimeCapsuleDetailService(
    private val fileService: FileService,
    private val timeCapsuleReader: TimeCapsuleReader,
    private val timeCapsuleLikeReader: TimeCapsuleLikeReader,
    private val timeCapsuleUserReader: TimeCapsuleUserReader,
    private val letterReader: LetterReader,
) {
    fun getTimeCapsuleDetail(
        capsuleId: Long,
        userId: Long,
    ): TimeCapsuleDetailDto {
        val capsule = timeCapsuleReader.getById(capsuleId)
        val now = LocalDateTime.now()

        val liked = timeCapsuleLikeReader.findByUserIdAndCapsuleId(userId, capsuleId)?.isLiked ?: false
        val status = capsule.getStatus(now)
        val remainingTime = RemainingTimeDto.fromStatus(status, now, capsule.openAt, capsule.closedAt)
        val likeCount = timeCapsuleLikeReader.getLikeCount(capsuleId)
        val participantCount = timeCapsuleUserReader.getParticipantCount(capsuleId)
        val letterCount = letterReader.getLetterCountByCapsuleId(capsule.id)
        val isMine = capsule.creator.id == userId
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
            inviteCode = capsule.inviteCode,
            beadVideoUrl = beadVideoUrl,
        )
    }

    fun getMyTimeCapsules(
        userId: Long,
        limit: Int,
    ): List<TimeCapsuleSummaryDto> {
        val pageable = PageRequest.of(0, limit)
        val capsules = timeCapsuleReader.getMyTimeCapsules(userId, pageable)
        return getTimeCapsuleSummaries(capsules, LocalDateTime.now())
    }

    fun getPopularTimeCapsules(limit: Int): List<TimeCapsuleSummaryDto> {
        val pageable = PageRequest.of(0, limit)
        val capsules = timeCapsuleReader.getPopularTimeCapsules(pageable)
        return getTimeCapsuleSummaries(capsules, LocalDateTime.now())
    }

    private fun getTimeCapsuleSummaries(
        capsules: List<TimeCapsule>,
        now: LocalDateTime,
    ): List<TimeCapsuleSummaryDto> {
        val capsuleIds = capsules.map { it.id }
        val participantCountMap = timeCapsuleUserReader.getParticipantCountMap(capsuleIds)
        val letterCountMap = letterReader.getLetterCountMap(capsuleIds)

        return capsules.map { capsule ->
            TimeCapsuleSummaryDto(
                id = capsule.id,
                title = capsule.title,
                participantCount = participantCountMap[capsule.id] ?: 0,
                letterCount = letterCountMap[capsule.id] ?: 0,
                remainingStatus = RemainingStatusDto.of(capsule.openAt, now, capsule.getStatus(now)),
            )
        }
    }

    private fun getBeadObjectKey(letterCount: Int): String {
        val beadIndex = if (letterCount >= 100) 10 else letterCount / 10
        return "CAPSULE/detail_bead$beadIndex.mp4"
    }
}
