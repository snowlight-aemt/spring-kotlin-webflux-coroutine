package me.snowlight.springkotlincoroutine.config

import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerRequest

private val logger = KotlinLogging.logger {}

@Configuration
class ErrorConfig {
    @Bean
    fun errorAttribute(): DefaultErrorAttributes {
        // LEARN request 는 고유의 헤쉬 오브젝트가 있다.
        //  (Identifier HashCode) = 오브젝트 고유 식별자 (Instance 별로 고유하다.)
        return object: DefaultErrorAttributes() {
            override fun getErrorAttributes(
                serverRequest: ServerRequest,
                options: ErrorAttributeOptions?
            ): MutableMap<String, Any> {

                val request = serverRequest.exchange().request
                val txId = request.txid ?: ""

                MDC.put(KEY_TXID, txId)

                try {
                    logger.debug { "request id : ${serverRequest.exchange().request.id}" }

                    super.getError(serverRequest).let { e ->
                        logger.error(e.message ?: "Internal Server Error", e)
                    }
                    return super.getErrorAttributes(serverRequest, options).apply {
                        remove("requestId")
                        put(KEY_TXID, txId)
                    }
                } finally {
                    request.txid = null
                    MDC.remove(KEY_TXID)
                }
            }
        }
    }
}