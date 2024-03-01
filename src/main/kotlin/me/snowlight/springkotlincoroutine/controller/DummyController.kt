package me.snowlight.springkotlincoroutine.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class DummyController {
    @GetMapping("/circuit/child/{flag}", "/circuit/child", "/circuit/child/")
    suspend fun testCircuitBreaker(@PathVariable flag: String?): String {
        if (flag?.lowercase() == "n") {
            throw RuntimeException("fail on child")
        } else {
            return "success"
        }
    }
}
