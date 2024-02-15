package me.snowlight.springkotlincoroutine.service

import kotlinx.coroutines.flow.Flow
import me.snowlight.springkotlincoroutine.exception.NoArticleFound
import me.snowlight.springkotlincoroutine.model.Article
import me.snowlight.springkotlincoroutine.model.ArticleRepository
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val repository: ArticleRepository,
) {
    suspend fun create(request: ReqCreate): Article {
        return repository.save(request.toArticle())
    }

    suspend fun get(id: Long): Article {
        return repository.findById(id) ?: throw NoArticleFound("id: $id")
    }

    suspend fun getAll(title: String? = null): Flow<Article> {
        return if (title.isNullOrEmpty()) {
            repository.findAll()
        } else {
            repository.findAllByTitleContains(title);
        }
    }
}