package me.snowlight.springkotlincoroutine.config

import kotlinx.coroutines.slf4j.MDCContext
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.KotlinDetector
import org.springframework.stereotype.Component
import kotlin.coroutines.Continuation

@Aspect
@Component
class AspectConfig {

    // LEARN 아직 suspend fun 를 처리하지 못한다. (이거 때문에 suspend fun 필요 : withContext(MDCContext()) {...})
    //  그래서 강제로 suspend fun 를 사용할 수 있도록 AOP 에서 조작
    @Around("""
        @annotation(org.springframework.web.bind.annotation.GetMapping) ||
        @annotation(org.springframework.web.bind.annotation.PostMapping) ||
        @annotation(org.springframework.web.bind.annotation.PutMapping) ||
        @annotation(org.springframework.web.bind.annotation.DeleteMapping) ||
        @annotation(org.springframework.web.bind.annotation.PatchMapping)||
        @annotation(org.springframework.web.bind.annotation.RequestMapping)  
    """)
    fun bindMdcContext(jp: ProceedingJoinPoint): Any? {
        return if (jp.hasSuspendFunction) {
            val continuation = jp.args.last() as Continuation<*>
            val newContext = continuation.context + (MDCContext())
            val newContinuation = Continuation(newContext) { continuation.resumeWith(it)}

            val newArgs = jp.args.dropLast(1) + newContinuation
            jp.proceed(newArgs.toTypedArray())
        } else {
            jp.proceed()
        }
    }

    private val ProceedingJoinPoint.hasSuspendFunction: Boolean
        get() {
            val method = (this.signature as MethodSignature).method
            return KotlinDetector.isSuspendingFunction(method)
        }
}