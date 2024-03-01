package me.snowlight.springkotlincoroutine.example

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Range
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.redis.connection.DataType
import org.springframework.data.redis.connection.RedisGeoCommands
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveZSetOperations
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import java.util.Date
import java.util.NoSuchElementException
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {}

private const val KEY = "key"

@SpringBootTest
@ActiveProfiles("test")
class RedisTemplateTest(
    private val template: ReactiveRedisTemplate<Any, Any>,
): WithRedisContainer, StringSpec({
    afterTest {
        template.opsForValue().delete(KEY).awaitSingle()
    }

    "reactive redis template set" {
        val ops = template.opsForValue()

        shouldThrow<NoSuchElementException> {
            ops.get(KEY).awaitSingle()
        }
        ops.set(KEY, "aaaa").awaitSingle()
        ops.get(KEY).awaitSingle() shouldBe "aaaa";
    }

    "reactive redis template expire" {
        val ops = template.opsForValue()

        template.expire(KEY, 1.seconds.toJavaDuration()).awaitSingle()
        delay(3.seconds)
        shouldThrow<NoSuchElementException> {
            ops.get(KEY).awaitSingle()
        }
    }

    "redis LinkedList" {
        val ops = template.opsForList()
        ops.rightPushAll(KEY, 1, 2, 3, 4).awaitSingle()

        template.type(KEY).awaitSingle() shouldBe DataType.LIST
        ops.size(KEY).awaitSingle() shouldBe 4

//       for (i in 0..< ops.size(KEY).awaitSingle()) {
//            ops.index(KEY, i).awaitSingle().let {
//                logger.info { "$i: $it" }
//            }
//        }
//        ops.range(KEY, 0, -1).asFlow().collect { logger.debug { it } }
//        ops.range(KEY, 0, -1).toStream().forEach {logger.debug { it } }

        ops.all(KEY) shouldBe listOf(1,2,3,4)

    }

    "redis Queue" {
        val ops = template.opsForList()
        ops.rightPushAll(KEY, 1, 2, 3, 4).awaitSingle()

        ops.rightPush(KEY, 5).awaitSingle()
        ops.all(KEY) shouldBe listOf(1,2,3,4,5)

        ops.leftPop(KEY).awaitSingle() shouldBe 1
        ops.all(KEY) shouldBe listOf(2,3,4,5)
    }

    "redis LRU (Least Recently Used Algorithm)" {
        val ops = template.opsForList()
        ops.rightPushAll(KEY, 9, 8, 7, 6, 5, 4, 3, 2, 1).awaitSingle()

        ops.remove(KEY, 0, 2).awaitSingle()
        ops.all(KEY) shouldBe listOf(9, 8, 7, 6, 5, 4, 3, 1)
        ops.leftPush(KEY, 2).awaitSingle()
        ops.all(KEY) shouldBe listOf(2, 9, 8, 7, 6, 5, 4, 3, 1)
    }

    "redis Hash" {
        val ops = template.opsForHash<Int, String>()
        val map = (1..10).map { it to "val-$it" }.toMap()
        ops.putAll(KEY, map).awaitSingle()

        ops.size(KEY).awaitSingle() shouldBe 10
        ops.get(KEY, 1).awaitSingle() shouldBe "val-1"
        ops.get(KEY, 8).awaitSingle() shouldBe "val-8"
    }

    "redis LRU (feat Sorted Set)" {
        val ops = template.opsForZSet()
        listOf(8, 7, 2, 4, 5, 22, 9, 14).forEach {
            ops.add(KEY, it, -1.0 * Date().time).awaitSingle()
            ops.all(KEY).let { logger.debug { it } }
        }
        ops.all(KEY) shouldBe listOf(14, 9, 22, 5, 4, 2, 7, 8)
    }

    "ranking (feat Sorted Set) " {
        val ops = template.opsForZSet()
        listOf(
            "aaa" to 111,
            "bbb" to 756,
            "ccc" to 967,
            "ddd" to 234,
            "aaa" to 543,
        ).also {
            it.toMap().toList().sortedBy { it.second }
        }.forEach {
            ops.add(KEY, it.first, it.second * 1.0).awaitSingle()
            ops.all(KEY).let { logger.info { it } }
        }

        ops.all(KEY) shouldBe listOf("ddd", "aaa", "bbb", "ccc")
    }

    "geo redis" {
        val ops = template.opsForGeo()
        listOf(
            RedisGeoCommands.GeoLocation("seoul", Point(126.97806, 37.56667)),
            RedisGeoCommands.GeoLocation("busan", Point(129.07556, 35.17944)),
            RedisGeoCommands.GeoLocation("incheon", Point(126.70528, 37.45639)),
            RedisGeoCommands.GeoLocation("daegu", Point(128.60250, 35.87222)),
            RedisGeoCommands.GeoLocation("anyang", Point(126.95556, 37.39444)),
            RedisGeoCommands.GeoLocation("daejeon", Point(127.38500, 36.35111)),
            RedisGeoCommands.GeoLocation("gwangju", Point(126.85306, 35.15972)),
            RedisGeoCommands.GeoLocation("suwon", Point(127.02861, 37.26389)),
        ).forEach {
            ops.add(KEY,it as RedisGeoCommands.GeoLocation<Any>).awaitSingle()
        }

        ops.distance(KEY, "seoul", "busan").awaitSingle().let { logger.info { it } }

        val point = ops.position(KEY, "daegu").awaitSingle().also { logger.debug { it } }
        val circle = Circle(point, Distance(200.0, Metrics.KILOMETERS))
        ops.radius(KEY, circle).asFlow().map { it.content.name }.toList().let {
            logger.info { "$it" }
        }
    }

    "hyper loglog" {
        val ops = template.opsForHyperLogLog()
        ops.add("page1", "192.179.0.23", "41.61.2.230", "225.105.161.131").awaitSingle()
        ops.add("page2", "1.1.1.1", "2.2.2.2", "3.3.3.3").awaitSingle()
        ops.add("page3", "7.7.7.7").awaitSingle()
        ops.add("page3", "8.8.8.8").awaitSingle()
        ops.add("page3", "1.1.1.1", "2.2.2.2", "7.7.7.7").awaitSingle()

        ops.size("page3").awaitSingle().let { logger.debug { it } }
    }

    "pub/sub" {
        template.listenToChannel("channel-1").doOnNext {
            logger.debug { ">> received 1: $it" }
        }.subscribe()

        template.listenToChannel("channel-1").doOnNext {
            logger.debug { ">> received 2: $it" }
        }.subscribe()

        template.listenToChannel("channel-1").asFlow().onEach {
            logger.debug { ">> received (Coroutine) 3: $it" }
        }.launchIn(CoroutineScope((Dispatchers.Default)))



        repeat(10) {
            val message = "test message (${it + 1})"
            logger.debug { ">> send: $message" }
            template.convertAndSend("channel-1", message).awaitSingle()
            delay(1000)
        }
    }
})

interface WithRedisContainer {
    companion object {
        private val container = GenericContainer(DockerImageName.parse("redis")).apply {
            addExposedPorts(6379)
            start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun setProperty(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.port") {
                "${container.getMappedPort(6379)}"
            }
        }
    }
}


suspend fun ReactiveListOperations<Any, Any>.all(key: Any)
    = this.range(key, 0,-1).asFlow().toList()

suspend fun ReactiveZSetOperations<Any, Any>.all(key: Any)
    = this.range(KEY, Range.closed(0, -1)).asFlow().toList()