package me.snowlight.springkotlincoroutine.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import me.snowlight.springkotlincoroutine.config.extension.toLocalDate
import me.snowlight.springkotlincoroutine.exception.NoArticleFound
import me.snowlight.springkotlincoroutine.model.Article
import me.snowlight.springkotlincoroutine.model.ArticleRepository
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties.Simple
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

// LEARN MVC , WebFlux , Coroutine 서비스의 코드 형태가 많이 다르다.
@Service
class ArticleService(
    private val repository: ArticleRepository,
    private val dbClient: DatabaseClient,
    redisTemplate: ReactiveRedisTemplate<Any, Any>,
) {
    // LEARN Thread safe 한가?
    private val ops = redisTemplate.opsForValue()

    suspend fun create(request: ReqCreate): Article {
        return repository.save(request.toArticle())
    }

    suspend fun get(id: Long): Article {
        val key = SimpleKey("/article/get", id)
        return ops.get(key).awaitSingleOrNull()?.let { it as Article }
                    ?: repository.findById(id)?.also { ops.set(key, it, 20.seconds.toJavaDuration()).awaitSingle() }
                    ?: throw NoArticleFound("id: $id")
    }

//    suspend fun getAll(title: String? = null): Flow<Article> {
//        return if (title.isNullOrEmpty()) {
//            repository.findAll()
//        } else {
//            repository.findAllByTitleContains(title);
//        }
//    }

    suspend fun getAll(request: QryArticle): Flow<Article> {
        val params = HashMap<String, Any>()
        var sql = dbClient.sql("""
            SELECT id, title, body, author_id, created_at, updated_at
            FROM TB_ARTICLE
            WHERE 1=1
                ${ request.title.query {
                    params["title"] = it.trim().let { "%$it%" }
                    "AND title LIKE :title"
                }}
                ${ request.authorId.query {
                    params["authorId"] = it
                    "AND author_id IN (:authorId)"
                }}
                ${ request.from.query {
                    params["from"] = it.toLocalDate()
                    "AND created_at >= :from"
                }}
                ${ request.to.query {
                    params["to"] = it.toLocalDate().plusDays(1)
                    "AND created_at < :to"
                }}
        """.trimIndent())
        params.forEach { key, value -> sql = sql.bind(key, value) }
        return sql.map { row ->
            Article(
                id = row.get("id") as Long,
                title = row.get("title") as String,
                body = row.get("body") as String?,
                authorId = row.get("author_id") as Long,
            ).apply {
                createdAt = row.get("created_at") as LocalDateTime?
                updatedAt = row.get("updated_at") as LocalDateTime?
            }
        }.flow()
    }

    suspend fun update(id: Long, request: ReqUpdate): Article {
        val article = repository.findById(id) ?: throw NoArticleFound("id: $id")
        return repository.save(article.apply {
            request.title?.let { title = it }
            request.body?.let { body = it }
            request.authorId?.let { authorId = it }
        }).also {
            val key = SimpleKey("/article/get", id)
            ops.delete(key).awaitSingle()
        }
    }

    suspend fun delete(id: Long) {
        return repository.deleteById(id).also {
            val key = SimpleKey("/article/get", id)
            ops.delete(key).awaitSingle()
        }
    }
}

// LEARN 함수 확장을 하는 예시
// if(request.title.isNullOrBlank()) "" else {
//     params["title"] = request.title.trim().let { "%$it%" }
//     "AND title LIKE :title"
// }
fun <T> T?.query(f: (T) -> String): String {
    return when {
        this == null -> ""
        this is String && this.isBlank() -> ""
        this is Collection<*> && this.isEmpty() -> ""
        this is Array<*> && this.isEmpty() -> ""
        else -> f.invoke(this)
    }
}