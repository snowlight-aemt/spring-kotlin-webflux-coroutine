package me.snowlight.springkotlincoroutine.controller

import kotlinx.coroutines.delay
import me.snowlight.springkotlincoroutine.service.AdvancedService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
class AdvancedController(
    private val service: AdvancedService,
) {

    @GetMapping("/test/mdc")
    suspend fun testRequestTxid() {
        logger.debug { "Start MDC Txid" }
        delay(100)
        service.mdc()
        logger.debug { "End MDC Txid" }
    }
    @GetMapping("/test/mdc/2")
    fun testRequestTxid2() {
        logger.debug { "seol" }
    }
}