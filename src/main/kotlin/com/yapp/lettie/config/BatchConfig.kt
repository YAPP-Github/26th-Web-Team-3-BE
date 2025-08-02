package com.yapp.lettie.config

import com.yapp.lettie.api.timecapsule.service.batch.TimeCapsuleTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
@EnableBatchProcessing
class BatchConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val timeCapsuleTasklet: TimeCapsuleTasklet,
) {
    @Bean
    fun openTimeCapsuleJob(): Job =
        JobBuilder("openTimeCapsuleJob", jobRepository)
            .start(openCapsuleStep())
            .build()

    @Bean
    fun openCapsuleStep(): Step =
        StepBuilder("openCapsuleStep", jobRepository)
            .tasklet(timeCapsuleTasklet, transactionManager)
            .build()
}
