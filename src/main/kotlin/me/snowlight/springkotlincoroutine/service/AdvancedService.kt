package me.snowlight.springkotlincoroutine.service

import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class AdvancedService {
    suspend fun mdc() {
        logger.debug { "mdc service !" }
    }
}