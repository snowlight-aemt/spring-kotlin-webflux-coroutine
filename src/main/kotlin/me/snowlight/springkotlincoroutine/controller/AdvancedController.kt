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
import org.springframework.http.HttpStatus
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

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
        @RequestBody @Valid regErrorTest: ReqErrorTest
    ) {
        logger.debug { "request" }

//        if (regErrorTest.message == "error") {
//            throw InvalidParameter(regErrorTest, regErrorTest::message, "custom code", "custom msg")
//        }
        throw RuntimeException("yahoo !")
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidParameter(request: Any, field: KProperty<*>, code: String = "", message: String = "") : BindException(
    WebDataBinder(request, request::class.simpleName!!).bindingResult.apply {
        rejectValue(field.name, code, message)
    }
)

data class ReqErrorTest (
    @field:NotEmpty
    val id: String?,
    val age: Int?,
    @field:DataString
    val birthday: String?,
    val message: String?,
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
