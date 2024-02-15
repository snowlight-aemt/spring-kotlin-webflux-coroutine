package me.snowlight.springkotlincoroutine.service

import me.snowlight.springkotlincoroutine.model.Article

data class ReqUpdate (
    val title: String? = null,
    val body: String? = null,
    val authorId: Long? = null,
)