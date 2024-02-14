package me.snowlight.springkotlincoroutine.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import me.snowlight.springkotlincoroutine.model.ArticleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ArticleServiceTest(
    @Autowired private val service: ArticleService,
    @Autowired private val repository: ArticleRepository,
) : StringSpec({
    "create and get" {
        val prev = repository.count()
        service.create(ReqCreate(title = "title 1", body = "body 1", authorId = 100))
        val curr = repository.count()

        curr shouldBe prev + 1
    }
})
