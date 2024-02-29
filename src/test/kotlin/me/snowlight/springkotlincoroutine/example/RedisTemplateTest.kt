package me.snowlight.springkotlincoroutine.example

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class RedisTemplateTest(
    private val redisTemplate: ReactiveRedisTemplate<Any, Any>,
): StringSpec({
    "helle reactive redis" {
        val ops = redisTemplate.opsForValue()
        ops.set("key", "aaaa")
        ops.get("key") shouldBe "aaaa";
    }
})