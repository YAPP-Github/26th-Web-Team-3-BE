package com.yapp.lettie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class LettieApplication

fun main(args: Array<String>) {
    runApplication<LettieApplication>(*args)
}
