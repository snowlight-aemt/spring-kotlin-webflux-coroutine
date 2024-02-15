package me.snowlight.springkotlincoroutine.controller

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import me.snowlight.springkotlincoroutine.model.Article
import me.snowlight.springkotlincoroutine.model.ArticleRepository
import me.snowlight.springkotlincoroutine.service.ArticleService
import me.snowlight.springkotlincoroutine.service.ReqCreate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.temporal.ChronoUnit

// LEARN 리액트 에서는 WebTestClient 로 테스트를 해야 한다.
@SpringBootTest
class ArticleControllerTest(
    @Autowired private val service: ArticleService,
    @Autowired private val repository: ArticleRepository,
    @Autowired private val context: ApplicationContext,
) : StringSpec({

    val client = WebTestClient.bindToApplicationContext(context).build()

    beforeTest {
        repository.deleteAll()
    }

    fun getSize(title: String? = null) =
        client.get().uri("/article/all${title?.let { "?title=$it" } ?: ""}").accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectBody(List::class.java)
            .returnResult().responseBody?.size ?: 0

    "create" {
        client.post().uri("/article").accept(MediaType.APPLICATION_JSON)
            .bodyValue(ReqCreate(title = "AAAA", body = "BBBB", 222L))
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("title").isEqualTo("AAAA")
            .jsonPath("body").isEqualTo("BBBB")
    }


    "get" {
        val created = client.post().uri("/article").accept(MediaType.APPLICATION_JSON)
            .bodyValue(ReqCreate(title = "AAAA", body = "BBBB", 222L))
            .exchange()
            .expectBody(Article::class.java)
            .returnResult().responseBody!!

        val read = client.get().uri("/article/${created.id}").accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(Article::class.java)
            .returnResult().responseBody!!

        read.id shouldBe created.id
        read.title shouldBe created.title
        read.body shouldBe created.body
        read.createdAt?.truncatedTo(ChronoUnit.SECONDS) shouldBe created.createdAt?.truncatedTo(ChronoUnit.SECONDS)
        read.updatedAt?.truncatedTo(ChronoUnit.SECONDS) shouldBe created.updatedAt?.truncatedTo(ChronoUnit.SECONDS)
    }

    "get all" {
        service.create(ReqCreate("title 1", "body 1", 123L))
        service.create(ReqCreate("title 2", "body 2", 123L))
        service.create(ReqCreate("title 3", "body 3", 123L))

        val size = getSize()
        size shouldBe 3

        val sizeByTitle = getSize("title 2")
        sizeByTitle shouldBe 1
    }

    "update" {
        val create = service.create(ReqCreate("title 1", "body 1", 123L))

        client.put().uri("/article/${create.id}").accept(MediaType.APPLICATION_JSON)
            .bodyValue(ReqCreate(title = "AAAA", body = "BBBB", 222L))
            .exchange()
            .expectStatus().isCreated
            .expectBody()

        val read = client.get().uri("/article/${create.id}").accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("title").isEqualTo("AAAA")
            .jsonPath("body").isEqualTo("BBBB")
    }

    "delete" {
        val create = service.create(ReqCreate("title 1", "body 1", 123L))
        val prevCount = repository.count()

        client.delete().uri("/article/${create.id}").accept(MediaType.APPLICATION_JSON)
            .exchange()

        val currCount = repository.count()

        currCount shouldBe prevCount - 1
        repository.existsById(create.id) shouldBe false
    }
})


