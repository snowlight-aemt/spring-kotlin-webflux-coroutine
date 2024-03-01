package me.snowlight.springkotlincoroutine.controller

import me.snowlight.springkotlincoroutine.model.Account
import me.snowlight.springkotlincoroutine.service.AccountService
import me.snowlight.springkotlincoroutine.service.ReqUpdateAccount
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    private val accountService: AccountService,
) {

    @GetMapping("/account/{id}")
    suspend fun get(@PathVariable id: Long): Account {
        return accountService.get(id)
    }

    @PostMapping("/account/{id}")
    suspend fun deposit(
        @PathVariable id: Long,
        @RequestBody request: ReqUpdateAccount,
    ): Account {
        return accountService.deposit(id, request.amount)
    }
}