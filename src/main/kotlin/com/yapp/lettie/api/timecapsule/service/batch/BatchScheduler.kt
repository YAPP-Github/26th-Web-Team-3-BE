package com.yapp.lettie.api.timecapsule.service.batch

import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BatchScheduler(
    private val jobLauncher: JobLauncher,
    private val openTimeCapsuleJob: Job,
) {
    private val logger = KotlinLogging.logger {}

    @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Seoul") // 매 5분 정각에 실행
    fun launchJob() {
        val jobParameters =
            JobParametersBuilder()
                .addLocalDateTime("launchTime", LocalDateTime.now())
                .toJobParameters()

        try {
            jobLauncher.run(openTimeCapsuleJob, jobParameters)
            logger.info { "openTimeCapsuleJob launched at ${LocalDateTime.now()}" }
        } catch (e: Exception) {
            logger.error(e) { "Job 실행 실패" }
        }
    }
}
