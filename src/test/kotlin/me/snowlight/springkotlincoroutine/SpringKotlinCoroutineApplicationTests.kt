package me.snowlight.springkotlincoroutine

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import me.snowlight.springkotlincoroutine.model.Article
import me.snowlight.springkotlincoroutine.model.ArticleRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SpringKotlinCoroutineApplicationTests(
    @Autowired private val repository: ArticleRepository
): StringSpec({
    "context load".config(false) {
        val prev = repository.count()
        val save = repository.save(Article(title = "title1"))
        val curr = repository.count()

        curr shouldBe prev + 1  // Assertions.assertEquals(prev + 1, curr)
    }
}) {
}
