package me.snowlight.springkotlincoroutine.config

import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.*


private val logger = KotlinLogging.logger {}

@Component
@Order(1)
class MdcFilter: WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val uuid = exchange.request.headers["x-txid"]?.firstOrNull()?:"${UUID.randomUUID()}"
        // "%d{HH:mm:ss.SSS}|%highlight(%-5level)|%X{txid:-}|%green(%t)|%blue(\\(%F:%L\\))|%msg%n"
        logger.debug { "Filter" }
        MDC.put("txid", uuid)
        return chain.filter(exchange)
    }
}