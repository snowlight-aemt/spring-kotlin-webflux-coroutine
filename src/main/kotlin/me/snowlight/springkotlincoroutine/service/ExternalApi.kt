package me.snowlight.springkotlincoroutine.service

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.kotlin.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction
import io.github.resilience4j.kotlin.ratelimiter.RateLimiterConfig
import io.github.resilience4j.kotlin.ratelimiter.executeSuspendFunction
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RequestNotPermitted
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
        return try {
            rateLimiter.executeSuspendFunction {
                circuitBreaker.executeSuspendFunction {
                    logger.debug { "2. call external "}
                    client.get().uri("/circuit/child/$flag").retrieve().awaitBody()
                }
            }
        } catch (e: CallNotPermittedException) {
            "call layer (blocked by circuit breaker"
        } catch (e: RequestNotPermitted) {
            "call later (blocked by rate limiter)"
        }
    }

    /** LEARN CircuitBreaker
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

    /** LEARN Rate Limiter
     *   고도한 호출을 막기 위해서 (실패와 상관없음)
     */
    val rateLimiter = RateLimiter.of("rps-limiter", RateLimiterConfig {
        limitForPeriod(2)
        timeoutDuration(5.seconds.toJavaDuration())
        limitRefreshPeriod(10.seconds.toJavaDuration())
    })
}