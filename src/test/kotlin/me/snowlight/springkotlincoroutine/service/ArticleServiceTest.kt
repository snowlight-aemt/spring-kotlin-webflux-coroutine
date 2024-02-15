package me.snowlight.springkotlincoroutine.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList
import me.snowlight.springkotlincoroutine.model.ArticleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ArticleServiceTest(
    @Autowired private val service: ArticleService,
    @Autowired private val repository: ArticleRepository,
) : StringSpec({

    beforeTest {
        // TODO 나중에 수정 필요.
        repository.deleteAll()
    }

    "create and get" {
        val prev = repository.count()
        val created = service.create(ReqCreate(title = "title 1", body = "body 1", authorId = 100))
        val curr = repository.count()

        val get = service.get(created.id)

        curr shouldBe prev + 1
        get.id shouldBe created.id
        get.title shouldBe created.title
        get.authorId shouldBe created.authorId
        get.createdAt shouldNotBe null
        get.updatedAt shouldNotBe null
    }

    "get all" {
        service.create(ReqCreate(title = "title 1", body = "body 1", authorId = 100))
        service.create(ReqCreate(title = "title 2", body = "body 2", authorId = 200))
        service.create(ReqCreate(title = "title matched", body = "body 3", authorId = 300))

        // toList : Flow 를 다 모아서 list 를 만드다.
        service.getAll().toList().size shouldBe 3
        service.getAll("title matched").toList().size shouldBe 1
    }

    "update" {
        val created = service.create(ReqCreate(title = "title 1", body = "body 1", authorId = 100))

        val update = service.update(created.id, ReqUpdate(title = "aaaa", body = "bbbb", authorId = 9999))

        val get = service . get (created.id)

        get.id shouldBe update.id
        get.title shouldBe update.title
        get.authorId shouldBe update.authorId
        get.createdAt shouldNotBe null
        get.updatedAt shouldNotBe null
    }

    "delete" {
        val prevCnt  =repository.count()
        val created = service.create(ReqCreate(title = "title 1", body = "body 1", authorId = 100))
        val currCnt  =repository.count()

        repository.count() shouldBe prevCnt + 1
        service.delete(created.id)
        repository.count() shouldBe prevCnt
    }
})
