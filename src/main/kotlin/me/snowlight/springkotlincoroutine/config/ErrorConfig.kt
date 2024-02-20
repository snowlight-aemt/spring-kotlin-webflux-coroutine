package me.snowlight.springkotlincoroutine.config

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerRequest

@Configuration
class ErrorConfig {
    fun errorAttribute(): DefaultErrorAttributes {
        return object: DefaultErrorAttributes() {
            override fun getErrorAttributes(
                request: ServerRequest?,
                options: ErrorAttributeOptions?
            ): MutableMap<String, Any> {
                return super.getErrorAttributes(request, options)
            }
        }
    }
}