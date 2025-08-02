package com.yapp.lettie.api.timecapsule.service.batch

import com.yapp.lettie.api.email.service.EmailService
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleUserReader
import mu.KotlinLogging
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TimeCapsuleTasklet(
    private val timeCapsuleReader: TimeCapsuleReader,
    private val timeCapsuleUserReader: TimeCapsuleUserReader,
    private val emailService: EmailService,
    @Value("\${lettie.domain.name}") private val domainName: String,
) : Tasklet {
    private val logger = KotlinLogging.logger {}

    override fun execute(
        contribution: StepContribution,
        chunkContext: ChunkContext,
    ): RepeatStatus {
        val now = LocalDateTime.now().withSecond(0).withNano(0)
        val previousCheckTime = now.minusMinutes(CHECK_INTERVAL_MINUTES)

        val capsulesToOpen =
            timeCapsuleReader.findCapsulesToOpen(previousCheckTime, now)

        val capsuleIds = capsulesToOpen.map { it.id }
        val emailMap = timeCapsuleUserReader.getEmailsGroupByCapsuleId(capsuleIds)

        capsulesToOpen.forEach { capsule ->
            val recipients = emailMap[capsule.id] ?: emptyList()

            emailService.sendTimeCapsuleOpenedEmail(
                recipients = recipients,
                capsuleTitle = capsule.title,
                openDate = capsule.openAt.toLocalDate().toString(),
                capsuleLink = generateCapsuleLink(capsule.inviteCode),
            )

            logger.info { "Capsule(${capsule.id}) 오픈 처리 완료, 이메일 전송 완료 (${recipients.size}명)" }
        }

        return RepeatStatus.FINISHED
    }

    private fun generateCapsuleLink(inviteCode: String): String = "$domainName/capsules/$inviteCode"

    companion object {
        private const val CHECK_INTERVAL_MINUTES = 5L
    }
}
