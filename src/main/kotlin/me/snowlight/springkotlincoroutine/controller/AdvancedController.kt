package me.snowlight.springkotlincoroutine.controller

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import kotlinx.coroutines.delay
import me.snowlight.springkotlincoroutine.service.AdvancedService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

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
    suspend fun error(@RequestBody @Valid regErrorTest: ReqErrorTest) {
        logger.debug { "request" }
//        throw RuntimeException("yahoo !")
    }
}

data class ReqErrorTest (
    @field:NotEmpty
    val id: String?,
    val age: Int?,
    @field:DataString
    val birthday: String?
)

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [DateValidator::class])
annotation class DataString(
    val message: String = "not a valid date",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
class DateValidator: ConstraintValidator<DataString, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        val text = value?.filter { it.isDigit() } ?: return true
        return kotlin.runCatching {
            LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyyMMdd")).let {
                if(text != it.format(DateTimeFormatter.ofPattern("yyyyMMdd"))) null else true
            }
        }.getOrNull() != null

    }

}
