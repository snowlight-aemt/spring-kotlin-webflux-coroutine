package me.snowlight.springkotlincoroutine.service

import me.snowlight.springkotlincoroutine.model.Article

data class ReqCreate (
    val title: String,
    val body: String? = null,
    val authorId: Long,
) {
    fun toArticle(): Article {
        return Article(
            title = this.title,
            body = this.body,
            authorId = this.authorId
        )
    }
}
