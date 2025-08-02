package com.yapp.lettie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAsync
@EnableScheduling
class LettieApplication

fun main(args: Array<String>) {
    runApplication<LettieApplication>(*args)
}
