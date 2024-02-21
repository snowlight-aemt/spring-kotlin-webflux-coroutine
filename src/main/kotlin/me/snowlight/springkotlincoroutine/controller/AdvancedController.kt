package me.snowlight.springkotlincoroutine.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import kotlinx.coroutines.delay
import me.snowlight.springkotlincoroutine.config.validator.DataString
import me.snowlight.springkotlincoroutine.service.AdvancedService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
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

    @PutMapping("/test/error")
    suspend fun error(
        // Valid 예제 - DateValidator
        @RequestBody @Valid regErrorTest: ReqErrorTest
    ) {
        logger.debug { "request" }

        // BindResult 예제 - trace ID (MDC)
        // if (regErrorTest.message == "error") {
        //  throw InvalidParameter(regErrorTest, regErrorTest::message, "custom code", "custom msg")
        // }

        // DefaultErrorAttributes 예제 - trace ID (MDC)
        // throw RuntimeException("yahoo !")
    }
}

data class ReqErrorTest (
    @field:NotEmpty
    val id: String?,
    val age: Int?,
    @field:DataString
    val birthday: String?,
    val message: String?,
)
