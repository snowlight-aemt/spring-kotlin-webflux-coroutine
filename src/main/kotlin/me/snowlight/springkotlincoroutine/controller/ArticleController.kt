package me.snowlight.springkotlincoroutine.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleController {
    @GetMapping("/")
    suspend fun index(): String {
        return "main page"
    }
}