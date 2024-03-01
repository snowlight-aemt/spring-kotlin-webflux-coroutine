package me.snowlight.springkotlincoroutine.service

import kotlinx.coroutines.delay
import me.snowlight.springkotlincoroutine.exception.NoAccountFound
import me.snowlight.springkotlincoroutine.model.Account
import me.snowlight.springkotlincoroutine.model.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
    private val accountRepository: AccountRepository,
) {
    suspend fun get(id: Long): Account {
        return accountRepository.findById(id)?: throw NoAccountFound("id: $id")
    }

    @Transactional
    suspend fun deposit(id: Long, amount: Long): Account {
        return accountRepository.findLockedById(id)?.let {
//        return accountRepository.findById(id)?.let {
            delay(3000)
            it.balance += amount
            accountRepository.save(it)
        } ?: throw NoAccountFound("id: $id")
    }
}