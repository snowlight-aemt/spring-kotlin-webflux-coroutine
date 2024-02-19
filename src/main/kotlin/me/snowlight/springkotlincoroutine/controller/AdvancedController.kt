package me.snowlight.springkotlincoroutine.controller

import kotlinx.coroutines.delay
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
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
        withContext(MDCContext()) {
            logger.debug { "Start MDC Txid" }
            delay(100)
            service.mdc()
            logger.debug { "End MDC Txid" }
        }
    }
    /*
22:16:09.009|DEBUG||reactor-http-nio-2|(MdcFilter.kt:22)|Filter
22:16:09.049|INFO ||task-1|(AdvancedController.kt:17)|Hello MDC Txid
     */
}