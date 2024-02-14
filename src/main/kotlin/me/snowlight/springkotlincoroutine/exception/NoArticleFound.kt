package me.snowlight.springkotlincoroutine.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NoArticleFound(message: String) : Throwable(message)
