package me.snowlight.springkotlincoroutine.model

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : CoroutineCrudRepository<Account, Long> {
//    @Lock(LockMode.PESSIMISTIC_WRITE)
    suspend fun findLockedById(id: Long): Account?
}