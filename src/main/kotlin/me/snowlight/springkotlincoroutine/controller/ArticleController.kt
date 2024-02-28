package me.snowlight.springkotlincoroutine.controller

import kotlinx.coroutines.flow.Flow
import me.snowlight.springkotlincoroutine.model.Article
import me.snowlight.springkotlincoroutine.service.ArticleService
import me.snowlight.springkotlincoroutine.service.QryArticle
import me.snowlight.springkotlincoroutine.service.ReqCreate
import me.snowlight.springkotlincoroutine.service.ReqUpdate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/article")
class ArticleController(
    private val service: ArticleService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody request: ReqCreate): Article {
        return service.create(request)
    }

    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: Long): Article {
        return service.get(id)
    }

//    @GetMapping("/all")
//    suspend fun getAll(title: String): Flow<Article> {
//        return service.getAll(title)
//    }

    @GetMapping("/all")
    suspend fun getAll(request: QryArticle): Flow<Article> {
        return service.getAll(request)
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun update(
        @PathVariable id: Long,
        @RequestBody request: ReqUpdate,
    ): Article {
        return service.update(id, request)
    }

    @DeleteMapping("/{id}")
    suspend fun delete(
        @PathVariable id: Long
    ) {
        return service.delete(id)
    }
}