package me.snowlight.springkotlincoroutine.controller

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import me.snowlight.springkotlincoroutine.model.ArticleRepository
import me.snowlight.springkotlincoroutine.service.rollback
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.ScriptUtils
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.reactive.TransactionalOperator

@SpringBootTest
@ActiveProfiles("test")
class ScriptTest(
    @Autowired private val client: DatabaseClient,
    @Autowired private val repository: ArticleRepository,
    @Autowired private val rxtx: TransactionalOperator
): StringSpec ({
//    beforeSpec {
//        val script = ClassPathResource("test.sql")
//        client.inConnection { conn ->
//            ScriptUtils.executeSqlScript(conn, script)
//        }.subscribe()
//    }
//
//    "check script" {
//        rxtx.rollback {
//            repository.count() shouldBe 3
//        }
//    }
})