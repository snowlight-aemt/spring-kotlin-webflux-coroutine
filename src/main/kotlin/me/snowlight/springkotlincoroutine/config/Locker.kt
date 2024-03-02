package me.snowlight.springkotlincoroutine.config

import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeoutException
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {}

@Component
class Locker(
    private val template: ReactiveRedisTemplate<Any, Any>,
) {
    private val ops = template.opsForValue()

    suspend fun <T> lock(key: SimpleKey, work: suspend () -> T):T {
        if (!tryLock(key))
            throw TimeoutException("fail to obtain lock ($key)")
        try {
            return work.invoke()
        } finally {
            unLock(key)
        }
    }

    private suspend fun tryLock(key: SimpleKey): Boolean {
        val start = System.nanoTime()
        while (!ops.setIfAbsent(key, true, 30.seconds.toJavaDuration()).awaitSingle()) {
            delay(100)
            val elapsed = (System.nanoTime() - start).nanoseconds
            if (elapsed >= 10.seconds) {
                return false
            }
        }
        return true;
    }

    private suspend fun unLock(key: SimpleKey) {
        try {
            ops.delete(key).awaitSingle()
        } catch (e:Exception) {
            logger.warn(e.message, e)
        }
    }
}