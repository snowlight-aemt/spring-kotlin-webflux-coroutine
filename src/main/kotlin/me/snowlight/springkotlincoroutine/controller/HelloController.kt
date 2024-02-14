package me.snowlight.springkotlincoroutine.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping("/")
    suspend fun index(): String {
        return "main page"
    }

    @GetMapping("/hello")
    suspend fun hello(name: String?): String {
        return "hello page $name"
    }
}