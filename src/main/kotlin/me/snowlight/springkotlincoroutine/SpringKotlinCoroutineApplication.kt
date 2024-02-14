package me.snowlight.springkotlincoroutine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringKotlinCoroutineApplication

fun main(args: Array<String>) {
    runApplication<SpringKotlinCoroutineApplication>(*args)
}
