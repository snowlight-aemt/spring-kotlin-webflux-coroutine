package me.snowlight.springkotlincoroutine.service

import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import me.snowlight.springkotlincoroutine.model.ArticleRepository
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Schema
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import kotlin.math.log

private val logger = KotlinLogging.logger {}

@Service
class AdvancedService(
    private val repository: ArticleRepository,
) {
    suspend fun mdc() {
        logger.debug { "S 1 - mdc service !" }
        mdc2()
        logger.debug { "E 1 - mdc service !" }
    }
    suspend fun mdc2() {
        logger.debug { "S 2 - mdc service !" }
        delay(100)
        repository.findById(1).let {
            logger.debug { "article: $it" }
        }
        Mono.fromCallable {
            logger.debug { "reactor call !!" }
        }.subscribeOn(Schedulers.boundedElastic()).awaitSingle()
        logger.debug { "E 2 - mdc service !" }
    }
}