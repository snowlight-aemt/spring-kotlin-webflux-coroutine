package me.snowlight.springkotlincoroutine.service

import me.snowlight.springkotlincoroutine.config.validator.DataString
import java.io.Serializable

data class QryArticle (
    val title: String?,
    val authorId: List<Long>?,
    @DataString
    val from: String?,
    @DataString
    val to: String?,
): Serializable