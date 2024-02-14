package me.snowlight.springkotlincoroutine.model

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository: CoroutineCrudRepository<Article, Long>