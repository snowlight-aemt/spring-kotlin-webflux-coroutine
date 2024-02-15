package me.snowlight.springkotlincoroutine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@SpringBootApplication
@EnableR2dbcAuditing
class SpringKotlinCoroutineApplication

fun main(args: Array<String>) {
    runApplication<SpringKotlinCoroutineApplication>(*args)
}
