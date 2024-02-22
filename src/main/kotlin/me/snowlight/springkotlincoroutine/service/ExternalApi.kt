package me.snowlight.springkotlincoroutine.service

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.kotlin.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {}

@Service
class ExternalApi(
    @Value("\${api.externalUrl}") private val externalUrl: String
) {
    private val client = WebClient.builder().baseUrl(externalUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    suspend fun delay() {
        return client.get().uri("/delay").retrieve().awaitBody()
    }

    suspend fun testCircuitBreaker(flag: String): String {
        logger.debug { "1. request call" }
        return circuitBreaker.executeSuspendFunction {
            logger.debug { "2. call external "}
             try {
                 client.get().uri("/circuit/child/$flag").retrieve().awaitBody()
             } finally {
                 logger.debug {" 3. done "}
             }
        }
    }

    /** LEARN
     *   close : 회로가 닫힘 - > 장상
     *   open : 회로가 열림 - > 차단
     *   half-open : 반 열림
     */
    val circuitBreaker = CircuitBreaker.of("test", CircuitBreakerConfig {
        slidingWindowSize(10)
        failureRateThreshold(20.0F)
        // open (차단) 후 몇 초 후 close (열림) 상태로 변경 : half-open
        waitDurationInOpenState(10.seconds.toJavaDuration())
        // half-open 상태에서 허용할 요청 수
        permittedNumberOfCallsInHalfOpenState(3)
    })
}